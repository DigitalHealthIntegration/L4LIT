package ca.uhn.fhir.jpa.starter.customOperations.r4

import ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping.R4StructureMapExtractionContext
import ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping.targetStructureMap
import ca.uhn.fhir.jpa.starter.customOperations.services.HelperService
import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.Operation
import ca.uhn.fhir.rest.annotation.OperationParam
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.StructureMap
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.utils.StructureMapUtilities
import org.hl7.fhir.utilities.npm.NpmPackage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class ExtractOperationProvider {

    @Autowired
    private lateinit var helperService: HelperService

    @Autowired
    private lateinit var r4FhirOperationHelper: R4FhirOperationHelper

    private val logger = LoggerFactory.getLogger(ExtractOperationProvider::class.java)

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
                val questionnaire =
                    workerContext.fetchResource(Questionnaire::class.java, theQuestionnaireResponse.questionnaire)
                        ?: throw ResourceNotFoundException("Questionnaire Resource not found")

                val structureMap =
                    workerContext.fetchResource(StructureMap::class.java, questionnaire.targetStructureMap!!)
                        ?: throw ResourceNotFoundException("StructureMap Resource not found")

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
        } catch (resourceNotFoundException: ResourceNotFoundException) {
            // Handle ResourceNotFoundException
            logger.error("Resource not found: ${resourceNotFoundException.message}")
        } catch (ioException: IOException) {
            // Handle IOException
            logger.error("IO error occurred: ${ioException.message}")
        } catch (exception: Exception) {
            // Catch any other unexpected exceptions
            logger.error("An error occurred: ${exception.message}")
        }
        // Return empty Bundle
        return Bundle()
    }

}