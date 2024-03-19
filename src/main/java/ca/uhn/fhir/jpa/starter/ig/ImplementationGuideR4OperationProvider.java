package ca.uhn.fhir.jpa.starter.ig;

import ca.uhn.fhir.jpa.packages.IPackageInstallerSvc;
import ca.uhn.fhir.jpa.packages.PackageInstallationSpec;
import ca.uhn.fhir.jpa.starter.annotations.OnR4Condition;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.ReferenceParam;
import org.hl7.fhir.r4.model.Base64BinaryType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Parameters;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import ca.uhn.fhir.model.api.annotation.Description;
import ca.uhn.fhir.jpa.starter.customOperations.r4.R4FhirOperationHelper;
import java.io.IOException;

@Conditional({OnR4Condition.class, IgConfigCondition.class})
@Service
public class ImplementationGuideR4OperationProvider implements IImplementationGuideOperationProvider {

	R4FhirOperationHelper helper = new R4FhirOperationHelper();
	IPackageInstallerSvc packageInstallerSvc;

	public ImplementationGuideR4OperationProvider(IPackageInstallerSvc packageInstallerSvc) {
		this.packageInstallerSvc = packageInstallerSvc;
	}

	@Operation(name = "$install", typeName = "ImplementationGuide")
	public Parameters install(@OperationParam(name = "npmContent", min = 1, max = 1) Base64BinaryType implementationGuide) {
		try {

			packageInstallerSvc.install(IImplementationGuideOperationProvider.toPackageInstallationSpec(implementationGuide.getValue()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new Parameters();
	}

	@Operation(idempotent = true, name = "$hello-world")
	public OperationOutcome hello_world() {
		OperationOutcome outcome = new OperationOutcome();
		outcome.addIssue().setDiagnostics("Bye");
		return outcome;
	}

	@Operation(idempotent = true, name = "$apply")
	public IBaseResource applyPlanDefinition(
//		@IdParam IdType theId,
//		@OperationParam(name = "subject", min = 1, max = 1) String subject,
		RequestDetails theRequestDetails
	) {
//		String subjectId = theSubject.getIdPart();
		// Log the values of theId and subject
		System.out.println("ID: " + theRequestDetails);
		System.out.println("Subject: " + theRequestDetails);
//		IBaseResource output =
//			helper.generateCarePlan(
//				subjectId,
//				theId.toString(),
//				null, // encounterId
//				null, // practitionerId
//				null, // organizationId
//				null, // userType
//				null, // userLanguage
//				null, // userTaskContext
//				null, // setting
//				null // settingContext
//			);
//		return output;
		return null;
	}

//	@Operation(name = "$apply", typeName = "ImplementationGuide")
//	public Parameters apply(@OperationParam(name = "npmContent", min = 1, max = 1) Base64BinaryType implementationGuide) {
//		try {
//
//			IBaseResource output =
//				helper.generateCarePlan(subject = "Patient/$patientId", planDefinitionId = planDefinitionId)		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//		return new Parameters();
//	}

	@Operation(name = "$uninstall", typeName = "ImplementationGuide")
	public Parameters uninstall(@OperationParam(name = "name", min = 1, max = 1) String name, @OperationParam(name = "version", min = 1, max = 1) String version) {

		packageInstallerSvc.uninstall(new PackageInstallationSpec().setName(name).setVersion(version));
		return new Parameters();
	}

}
