package ca.uhn.fhir.jpa.starter.customOperations.r4;

import ca.uhn.fhir.context.BaseRuntimeChildDefinition;
import ca.uhn.fhir.context.BaseRuntimeElementCompositeDefinition;
import ca.uhn.fhir.context.BaseRuntimeElementDefinition;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.IValidationSupport;
import ca.uhn.fhir.context.support.ValidationSupportContext;
import ca.uhn.fhir.i18n.Msg;
import ca.uhn.fhir.interceptor.model.RequestPartitionId;
import ca.uhn.fhir.jpa.api.config.JpaStorageSettings;
import ca.uhn.fhir.jpa.api.dao.DaoRegistry;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.jpa.model.config.PartitionSettings;
import ca.uhn.fhir.jpa.packages.PackageInstallationSpec;
import ca.uhn.fhir.jpa.packages.PackageInstallerSvcImpl;
import ca.uhn.fhir.jpa.packages.IHapiPackageCacheManager;
import ca.uhn.fhir.jpa.packages.PackageInstallOutcomeJson;
import ca.uhn.fhir.jpa.packages.ImplementationGuideInstallationException;
import ca.uhn.fhir.jpa.packages.JpaPackageCache;
import ca.uhn.fhir.jpa.packages.loader.PackageResourceParsingSvc;
import ca.uhn.fhir.jpa.packages.util.PackageUtils;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.jpa.searchparam.registry.ISearchParamRegistryController;
import ca.uhn.fhir.jpa.searchparam.util.SearchParameterHelper;
import ca.uhn.fhir.jpa.starter.customOperations.services.HelperService;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.api.server.SystemRequestDetails;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.util.FhirTerser;
import ca.uhn.fhir.util.SearchParameterUtil;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.utilities.json.model.JsonObject;
import org.hl7.fhir.utilities.npm.NpmPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class CustomPackageInstallerSvcImpl extends PackageInstallerSvcImpl {

	private static final Logger ourLog = LoggerFactory.getLogger(CustomPackageInstallerSvcImpl.class);

	boolean enabled = true;
	@Autowired
	private ISearchParamRegistryController mySearchParamRegistryController;

	@Autowired
	private FhirContext myFhirContext;

	@Autowired
	private DaoRegistry myDaoRegistry;

	@Autowired
	private IValidationSupport validationSupport;
	@Autowired
	private PackageResourceParsingSvc myPackageResourceParsingSvc;

	@Autowired
	private PartitionSettings myPartitionSettings;

	@Autowired
	private SearchParameterHelper mySearchParameterHelper;

	@Autowired
	private HelperService helperService;

	@Autowired
	private JpaStorageSettings myStorageSettings;

	@PostConstruct
	public void initialize() {
		switch (this.myFhirContext.getVersion().getVersion()) {
			case DSTU2:
			case DSTU2_HL7ORG:
			case DSTU2_1:
			default:
				ourLog.info("IG installation not supported for version: {}", this.myFhirContext.getVersion().getVersion());
				this.enabled = false;
			case R5:
			case R4B:
			case R4:
			case DSTU3:
		}
	}

	// Define a field to store the installed NpmPackage
	private NpmPackage installedPackage;

	@Autowired
	private IHapiPackageCacheManager myPackageCacheManager;

	@Override
	public PackageInstallOutcomeJson install(PackageInstallationSpec theInstallationSpec) throws ImplementationGuideInstallationException {
		PackageInstallOutcomeJson retVal = new PackageInstallOutcomeJson();

		try {
			NpmPackage npmPackage = this.myPackageCacheManager.installPackage(theInstallationSpec);
			helperService.installedNpmPackage = npmPackage;

			if (npmPackage == null) {
				throw new IOException(Msg.code(1284) + "Package not found");
			}

			retVal.getMessage().addAll(JpaPackageCache.getProcessingMessages(npmPackage));
			if (theInstallationSpec.isFetchDependencies()) {
				fetchAndInstallDependencies(npmPackage, theInstallationSpec, retVal);
			}

			if (theInstallationSpec.getInstallMode() == PackageInstallationSpec.InstallModeEnum.STORE_AND_INSTALL) {
				install(npmPackage, theInstallationSpec, retVal);
			}
			this.validationSupport.invalidateCaches();
		} catch (IOException var5) {
			throw new ImplementationGuideInstallationException(Msg.code(1285) + "Could not load NPM package " + theInstallationSpec.getName() + "#" + theInstallationSpec.getVersion(), var5);
		}

		return retVal;
	}


	// Custom method to call fetchAndInstallDependencies from superclass
	private void fetchAndInstallDependencies(NpmPackage npmPackage, PackageInstallationSpec theInstallationSpec, PackageInstallOutcomeJson theOutcome) throws ImplementationGuideInstallationException {
		if (npmPackage.getNpm().has("dependencies")) {
			JsonObject dependenciesElement = npmPackage.getNpm().get("dependencies").asJsonObject();
			Iterator var5 = dependenciesElement.getNames().iterator();

			while (var5.hasNext()) {
				String id = (String) var5.next();
				String ver = dependenciesElement.getJsonString(id).asString();

				try {
					List var10000 = theOutcome.getMessage();
					String var10001 = npmPackage.id();
					var10000.add("Package " + var10001 + "#" + npmPackage.version() + " depends on package " + id + "#" + ver);
					boolean skip = false;
					Iterator var9 = theInstallationSpec.getDependencyExcludes().iterator();

					while (var9.hasNext()) {
						String next = (String) var9.next();
						if (id.matches(next)) {
							theOutcome.getMessage().add("Not installing dependency " + id + " because it matches exclude criteria: " + next);
							skip = true;
							break;
						}
					}
					if (!skip) {
						NpmPackage dependency = this.myPackageCacheManager.loadPackage(id, ver);
						this.fetchAndInstallDependencies(dependency, theInstallationSpec, theOutcome);
						if (theInstallationSpec.getInstallMode() == PackageInstallationSpec.InstallModeEnum.STORE_AND_INSTALL) {
							this.install(dependency, theInstallationSpec, theOutcome);
						}
					}
				} catch (IOException var11) {
					throw new ImplementationGuideInstallationException(Msg.code(1287) + String.format("Cannot resolve dependency %s#%s", id, ver), var11);
				}
			}
		}
	}

	private boolean isStructureDefinitionWithoutSnapshot(IBaseResource r) {
		boolean retVal = false;
		FhirTerser terser = this.myFhirContext.newTerser();
		if (r.getClass().getSimpleName().equals("StructureDefinition")) {
			Optional<String> kind = terser.getSinglePrimitiveValue(r, "kind");
			if (kind.isPresent() && !((String) kind.get()).equals("logical")) {
				retVal = terser.getSingleValueOrNull(r, "snapshot") == null;
			}
		}
		return retVal;
	}

	private IBaseResource generateSnapshot(IBaseResource sd) {
		try {
			return this.validationSupport.generateSnapshot(new ValidationSupportContext(this.validationSupport), sd, (String) null, (String) null, (String) null);
		} catch (Exception var3) {
			throw new ImplementationGuideInstallationException(Msg.code(1290) + String.format("Failure when generating snapshot of StructureDefinition: %s", sd.getIdElement()), var3);
		}
	}

	private boolean isValidResourceStatusForPackageUpload(IBaseResource theResource) {
		if (!this.myStorageSettings.isValidateResourceStatusForPackageUpload()) {
			return true;
		} else {
			List<IPrimitiveType> statusTypes = this.myFhirContext.newFhirPath().evaluate(theResource, "status", IPrimitiveType.class);
			if (statusTypes.isEmpty()) {
				return true;
			} else if (((IPrimitiveType) statusTypes.get(0)).getValue() == null) {
				return false;
			} else {
				switch (theResource.fhirType()) {
					case "Subscription":
						return ((IPrimitiveType) statusTypes.get(0)).getValueAsString().equals("requested");
					case "DocumentReference":
					case "Communication":
						return !((IPrimitiveType) statusTypes.get(0)).getValueAsString().equals("?");
					default:
						return ((IPrimitiveType) statusTypes.get(0)).getValueAsString().equals("active");
				}
			}
		}
	}

	boolean validForUpload(IBaseResource theResource) {
		String resourceType = this.myFhirContext.getResourceType(theResource);
		if ("SearchParameter".equals(resourceType)) {
			String code = SearchParameterUtil.getCode(this.myFhirContext, theResource);
			if (!StringUtils.isBlank(code) && code.startsWith("_")) {
				ourLog.warn("Failed to validate resource of type {} with url {} - Error: Resource code starts with \"_\"", theResource.fhirType(), SearchParameterUtil.getURL(this.myFhirContext, theResource));
				return false;
			}

			String expression = SearchParameterUtil.getExpression(this.myFhirContext, theResource);
			if (StringUtils.isBlank(expression)) {
				ourLog.warn("Failed to validate resource of type {} with url {} - Error: Resource expression is blank", theResource.fhirType(), SearchParameterUtil.getURL(this.myFhirContext, theResource));
				return false;
			}

			if (SearchParameterUtil.getBaseAsStrings(this.myFhirContext, theResource).isEmpty()) {
				ourLog.warn("Failed to validate resource of type {} with url {} - Error: Resource base is empty", theResource.fhirType(), SearchParameterUtil.getURL(this.myFhirContext, theResource));
				return false;
			}
		}

		if (!this.isValidResourceStatusForPackageUpload(theResource)) {
			ourLog.warn("Failed to validate resource of type {} with ID {} - Error: Resource status not accepted value.", theResource.fhirType(), theResource.getIdElement().getValue());
			return false;
		} else {
			return true;
		}
	}

	private Object extractValue(IBase theResource, String thePath) {
		return this.myFhirContext.newTerser().getSingleValueOrNull(theResource, thePath);
	}

	private String extractSimpleValue(IBase theResource, String thePath) {
		IPrimitiveType<?> asPrimitiveType = (IPrimitiveType) this.extractValue(theResource, thePath);
		return (String) asPrimitiveType.getValue();
	}

	private String extractUniqeIdFromNamingSystem(IBaseResource theResource) {
		IBase uniqueIdComponent = (IBase) this.extractValue(theResource, "uniqueId");
		if (uniqueIdComponent == null) {
			throw new ImplementationGuideInstallationException(Msg.code(1291) + "NamingSystem does not have uniqueId component.");
		} else {
			return this.extractSimpleValue(uniqueIdComponent, "value");
		}
	}

	private boolean resourceHasUrlElement(IBaseResource resource) {
		BaseRuntimeElementDefinition<?> def = this.myFhirContext.getElementDefinition(resource.getClass());
		if (!(def instanceof BaseRuntimeElementCompositeDefinition)) {
			String var10002 = Msg.code(1293);
			throw new IllegalArgumentException(var10002 + "Resource is not a composite type: " + resource.getClass().getName());
		} else {
			BaseRuntimeElementCompositeDefinition<?> currentDef = (BaseRuntimeElementCompositeDefinition) def;
			BaseRuntimeChildDefinition nextDef = currentDef.getChildByName("url");
			return nextDef != null;
		}
	}

	private TokenParam extractIdentifierFromOtherResourceTypes(IBaseResource theResource) {
		Identifier identifier = (Identifier) this.extractValue(theResource, "identifier");
		if (identifier != null) {
			return new TokenParam(identifier.getSystem(), identifier.getValue());
		} else {
			throw new UnsupportedOperationException(Msg.code(1292) + "Resources in a package must have a url or identifier to be loaded by the package installer.");
		}
	}

	private SearchParameterMap buildSearchParameterMapForSearchParameter(IBaseResource theResource) {
		Optional<SearchParameterMap> spmFromCanonicalized = this.mySearchParameterHelper.buildSearchParameterMapFromCanonical(theResource);
		if (spmFromCanonicalized.isPresent()) {
			return (SearchParameterMap) spmFromCanonicalized.get();
		} else if (this.resourceHasUrlElement(theResource)) {
			String url = this.extractSimpleValue(theResource, "url");
			return SearchParameterMap.newSynchronous().add("url", new UriParam(url));
		} else {
			TokenParam identifierToken = this.extractIdentifierFromOtherResourceTypes(theResource);
			return SearchParameterMap.newSynchronous().add("identifier", identifierToken);
		}
	}

	private SearchParameterMap createSearchParameterMapFor(IBaseResource theResource) {
		String resourceType = theResource.getClass().getSimpleName();
		String url;
		if ("NamingSystem".equals(resourceType)) {
			url = this.extractUniqeIdFromNamingSystem(theResource);
			return SearchParameterMap.newSynchronous().add("value", (new StringParam(url)).setExact(true));
		} else if ("Subscription".equals(resourceType)) {
			url = this.extractSimpleValue(theResource, "id");
			return SearchParameterMap.newSynchronous().add("_id", new TokenParam(url));
		} else if ("SearchParameter".equals(resourceType)) {
			return this.buildSearchParameterMapForSearchParameter(theResource);
		} else if (this.resourceHasUrlElement(theResource)) {
			url = this.extractSimpleValue(theResource, "url");
			return SearchParameterMap.newSynchronous().add("url", new UriParam(url));
		} else {
			TokenParam identifierToken = this.extractIdentifierFromOtherResourceTypes(theResource);
			return SearchParameterMap.newSynchronous().add("identifier", identifierToken);
		}
	}

	private RequestDetails createRequestDetails() {
		SystemRequestDetails requestDetails = new SystemRequestDetails();
		if (this.myPartitionSettings.isPartitioningEnabled()) {
			requestDetails.setRequestPartitionId(RequestPartitionId.defaultPartition());
		}

		return requestDetails;
	}

	private IBundleProvider searchResource(IFhirResourceDao theDao, SearchParameterMap theMap) {
		return theDao.search(theMap, this.createRequestDetails());
	}

	@VisibleForTesting
	void install(IBaseResource theResource, PackageInstallationSpec theInstallationSpec, PackageInstallOutcomeJson theOutcome) {
		if (!this.validForUpload(theResource)) {
			ourLog.warn("Failed to upload resource of type {} with ID {} - Error: Resource failed validation", theResource.fhirType(), theResource.getIdElement().getValue());
		} else {
			IFhirResourceDao dao = this.myDaoRegistry.getResourceDao(theResource.getClass());
			SearchParameterMap map = this.createSearchParameterMapFor(theResource);
			IBundleProvider searchResult = this.searchResource(dao, map);
			String resourceQuery = map.toNormalizedQueryString(this.myFhirContext);
			if (!searchResult.isEmpty() && !theInstallationSpec.isReloadExisting()) {
				ourLog.info("Skipping update of existing resource matching {}", resourceQuery);
			} else {
				if (!searchResult.isEmpty()) {
					ourLog.info("Updating existing resource matching {}", resourceQuery);
				}

				IBaseResource existingResource = !searchResult.isEmpty() ? (IBaseResource) searchResult.getResources(0, 1).get(0) : null;
				boolean isInstalled = this.createOrUpdateResource(dao, theResource, existingResource);
				if (isInstalled) {
					theOutcome.incrementResourcesInstalled(this.myFhirContext.getResourceType(theResource));
				}
			}
		}
	}

	private void install(NpmPackage npmPackage, PackageInstallationSpec theInstallationSpec, PackageInstallOutcomeJson theOutcome) throws ImplementationGuideInstallationException {
		String name = npmPackage.getNpm().get("name").asJsonString().getValue();
		String version = npmPackage.getNpm().get("version").asJsonString().getValue();
		String fhirVersion = npmPackage.fhirVersion();
		String currentFhirVersion = this.myFhirContext.getVersion().getVersion().getFhirVersionString();
		this.assertFhirVersionsAreCompatible(fhirVersion, currentFhirVersion);
		List installTypes;
		if (!theInstallationSpec.getInstallResourceTypes().isEmpty()) {
			installTypes = theInstallationSpec.getInstallResourceTypes();
		} else {
			installTypes = PackageUtils.DEFAULT_INSTALL_TYPES;
		}
		ourLog.info("Installing package: {}#{}", name, version);
		int[] count = new int[installTypes.size()];
		int i;
		for (i = 0; i < installTypes.size(); ++i) {
			String type = (String) installTypes.get(i);
			Collection<IBaseResource> resources = this.myPackageResourceParsingSvc.parseResourcesOfType(type, npmPackage);
			count[i] = resources.size();
			Iterator var13 = resources.iterator();
			while (var13.hasNext()) {
				IBaseResource next = (IBaseResource) var13.next();
				try {
					next = this.isStructureDefinitionWithoutSnapshot(next) ? this.generateSnapshot(next) : next;
					this.install(next, theInstallationSpec, theOutcome);
				} catch (Exception var16) {
					ourLog.warn("Failed to upload resource of type {} with ID {} - Error: {}", new Object[]{this.myFhirContext.getResourceType(next), next.getIdElement().getValue(), var16.toString()});
					throw new ImplementationGuideInstallationException(Msg.code(1286) + String.format("Error installing IG %s#%s: %s", name, version, var16), var16);
				}
			}
		}
		ourLog.info(String.format("Finished installation of package %s#%s:", name, version));
		for (i = 0; i < count.length; ++i) {
			ourLog.info(String.format("-- Created or updated %s resources of type %s", count[i], installTypes.get(i)));
		}
	}
}