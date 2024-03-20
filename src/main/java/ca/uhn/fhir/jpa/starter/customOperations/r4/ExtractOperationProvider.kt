package ca.uhn.fhir.jpa.starter.customOperations.r4

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping.R4StructureMapExtractionContext
import ca.uhn.fhir.jpa.starter.customOperations.services.HelperService
import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.Operation
import ca.uhn.fhir.rest.annotation.OperationParam
import ca.uhn.fhir.rest.client.api.IGenericClient
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.*
import org.hl7.fhir.utilities.npm.NpmPackage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ExtractOperationProvider @Autowired constructor(private val helperService: HelperService) {
//    var helperService = HelperService()
    var r4FhirOperationHelper = R4FhirOperationHelper()
    var customPackageInstallerSvcImpl = CustomPackageInstallerSvcImpl()


    private val baseUrl = "http://localhost:8080/fhir"
    @Operation(name = "\$extract", idempotent = true, global = true, type = QuestionnaireResponse::class)
     fun extract(
        @IdParam theId: IdType,
        @OperationParam(name = "questionnaireId", min = 1, max = 1) questionnaireId: String,
        @OperationParam(name = "structureMapId", min = 1, max = 1) structureMapId: String
    ): Bundle {
        return runBlocking {

            // Retrieve the ID of the patient from theId parameter
            val questionnaireResponseId = theId.idPart
            val questionnaire = fetchResourceById(Questionnaire::class.java, questionnaireId)
            val questionnaireResponse = fetchResourceById(QuestionnaireResponse::class.java, questionnaireResponseId)
            val structureMap = fetchResourceById(StructureMap::class.java, structureMapId)
            println("questionnaire id is $questionnaireId")
            println("questionnaireResponseId id is $questionnaireResponseId")
            println("structureMap id is $structureMapId")
        val measlesOutbreakPackage = helperService.installedNpmPackage
            println("structureMap id is $measlesOutbreakPackage")
//            val measlesOutbreakPackage =
//                NpmPackage.fromPackage(helperService.readFileFromResources("/measles-outbreak/package.r4.tgz"))
            val basePackage = NpmPackage.fromPackage(helperService.readFileFromResources("/measles/package.tgz"))
            val workerContext = r4FhirOperationHelper.loadWorkerContext(measlesOutbreakPackage, basePackage)
            val transformSupportServices = R4TransformSupportServicesLM(workerContext, mutableListOf())

             questionnaire?.let { questionnaire ->
                if (questionnaireResponse != null) {
                    r4FhirOperationHelper.extract(
                        questionnaire,
                        questionnaireResponse,
                        structureMap?.let {
                            R4StructureMapExtractionContext(
                                transformSupportServices,
                                workerContext,
                                it
                            )
                        }
                    )
                } else {
                    Bundle() // Provide an alternative return value if questionnaireResponse is null
                }
            } ?: Bundle()
        }
    }

    private fun <T : Resource> fetchResourceById(resourceType: Class<T>, resourceId: String): T? {
        // Create a FHIR context
        val ctx = FhirContext.forR4()

        // Create a FHIR client
        val client: IGenericClient = ctx.newRestfulGenericClient(baseUrl)

        // Read the resource with the specified ID
        return client.read()
            .resource(resourceType)
            .withId(resourceId)
            .execute()
    }

}