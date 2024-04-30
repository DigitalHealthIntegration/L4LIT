package ca.uhn.fhir.jpa.starter.customOperations.r4

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.jpa.starter.AppProperties
import ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping.R4StructureMapExtractionContext
import ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping.targetStructureMap
import ca.uhn.fhir.jpa.starter.customOperations.services.HelperService
import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.Operation
import ca.uhn.fhir.rest.annotation.OperationParam
import ca.uhn.fhir.rest.client.api.IGenericClient
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.utils.StructureMapUtilities
import org.hl7.fhir.utilities.npm.NpmPackage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class ExtractOperationProvider @Autowired constructor(private val appProperties: AppProperties) {

    @Autowired
    private lateinit var helperService: HelperService

    @Autowired
    private lateinit var r4FhirOperationHelper: R4FhirOperationHelper

    private val logger = LoggerFactory.getLogger(ExtractOperationProvider::class.java)

    private val context: FhirContext = FhirContext.forR4()

    // Create a FHIR client
    private val client: IGenericClient = context.newRestfulGenericClient(appProperties.fhir_baseUrl)

    @Operation(name = "\$extract", idempotent = true, global = true, type = QuestionnaireResponse::class)
    fun extract(
        @IdParam theId: IdType,
        @OperationParam(name = "theQuestionnaireResponse") theQuestionnaireResponse: QuestionnaireResponse
    ): Bundle {
        try {
            return runBlocking {
                val measlesOutbreakPackage = helperService.installedNpmPackage
                val basePackage = NpmPackage.fromPackage(helperService.readFileFromResources("/measles/package.tgz"))
                val workerContext = r4FhirOperationHelper.loadWorkerContext(measlesOutbreakPackage, basePackage)
                val transformSupportServices = R4TransformSupportServicesLM(workerContext, mutableListOf())
                // Fetch resources
                var questionnaire =
                    workerContext.fetchResource(Questionnaire::class.java, theQuestionnaireResponse.questionnaire)
                if(questionnaire == null) {
                    questionnaire = fetchResourceById(Questionnaire::class.java, theQuestionnaireResponse.questionnaire)
                            ?: throw ResourceNotFoundException("Questionnaire Resource not found")
                }
                val structureMapExtensionValue = questionnaire.targetStructureMap
                        ?: throw ResourceNotFoundException("StructureMap Resource not found")

                var structureMap =
                    workerContext.fetchResource(StructureMap::class.java, structureMapExtensionValue)
                if(structureMap == null) {
                    val structureMapId = structureMapExtensionValue.split("/").lastOrNull()
                            ?: throw ResourceNotFoundException("StructureMap Resource not found")
                    structureMap = fetchResourceById(StructureMap::class.java, structureMapId)
                            ?: throw ResourceNotFoundException("StructureMap Resource not found")
                }

                r4FhirOperationHelper.extract(
                    questionnaire,
                    theQuestionnaireResponse,
                    R4StructureMapExtractionContext(
                        transformSupportServices,
                        workerContext
                    ) { x, worker ->
                        StructureMapUtilities(worker).parse(structureMap.toString(), x)
                    }
                )
            }
        }  catch (e: Exception) {
            e.printStackTrace()
            return handleException(e)
        }
    }

    private fun handleException(exception: Exception): Bundle {
        val message = when (exception) {
            is ResourceNotFoundException -> "Resource not found: ${exception.message}"
            is IOException -> "IO error occurred: ${exception.message}"
            else -> "An error occurred: ${exception.message}"
        }
        logger.error(message)
        val outcome = r4FhirOperationHelper.buildOperationOutcome(message)
        val bundle = Bundle()
        bundle.type = Bundle.BundleType.MESSAGE
        bundle.addEntry().resource = outcome
        return bundle
    }

    private fun <T : Resource> fetchResourceById(resourceType: Class<T>, resourceId: String): T? {
        return client.read()
                .resource(resourceType)
                .withId(resourceId)
                .execute()
    }



}