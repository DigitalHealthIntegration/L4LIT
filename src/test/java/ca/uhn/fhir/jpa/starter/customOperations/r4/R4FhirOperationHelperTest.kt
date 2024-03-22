//package ca.uhn.fhir.jpa.starter.customOperations.r4
//
//import ca.uhn.fhir.context.FhirContext
//import ca.uhn.fhir.context.FhirVersionEnum
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.databind.SerializationFeature
//import com.fasterxml.jackson.databind.json.JsonMapper
//import com.fasterxml.jackson.databind.node.ObjectNode
//import ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping.R4StructureMapExtractionContext
//import kotlinx.coroutines.runBlocking
//import org.hl7.fhir.r4.model.Patient
//import org.hl7.fhir.r4.model.Questionnaire
//import org.hl7.fhir.r4.model.QuestionnaireResponse
//import org.hl7.fhir.r4.model.ResourceType
//import org.hl7.fhir.r4.utils.StructureMapUtilities
//import org.hl7.fhir.utilities.npm.NpmPackage
//import org.junit.Assert.assertEquals
//import org.junit.Assert.assertNotNull
//import org.junit.Assert.assertTrue
//import org.junit.Test
//import java.io.InputStreamReader
//
//class R4FhirOperationHelperTest {
//    private val helper: R4FhirOperationHelper = R4FhirOperationHelper()
//    private val fhirContext = FhirContext.forR4()
//    private val jsonParser = fhirContext.newJsonParser()
//    private val iParser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()
//
//
//    @Test
//    fun generateCarePlan() {
//        val carePlan =
//            helper.generateCarePlan(
//                "Patient-Example",
//                "MedRequest-Example",
//            )
//
//        val expectedJson = readResourceAsString("/med_request_careplan.json")
//        val actualJson = jsonParser.encodeResourceToString(carePlan)
//
//        val normalizedExpectedJson = expectedJson?.let { normalizeJson(it) }
//        val normalizedActualJson = normalizeJson(actualJson)
//
//        assertEquals(normalizedExpectedJson, normalizedActualJson)
//    }
//
////    fun readResourceAsString(path: String) = open(path).readBytes().decodeToString()
//
//    fun readResourceAsString(path: String): String? {
//        return this.javaClass.getResourceAsStream(path)?.let { inputStream ->
//            InputStreamReader(inputStream).use {
//                it.readText()
//            }
//        }
//    }
//
//    private fun open(path: String) = javaClass.getResourceAsStream(path)!!
//
//     fun normalizeJson(json: String): String {
//        val mapper: ObjectMapper = JsonMapper.builder()
//            .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
//            .build()
//        val tree: ObjectNode = mapper.readTree(json) as ObjectNode
//        return mapper.writeValueAsString(tree)
//    }
//
////    fun parseJsonToResource(json: String): IBaseResource {
////        val parser = FhirContext.forR4().newJsonParser()
////        return parser.parseResource(json)
////    }
//
//
//
//    @Test
//    fun `extract() should perform StructureMap-based extraction using workerContext`():
//            Unit = runBlocking {
//        val questionnaireString =
//            readFileFromResourcesAsString("/measles-outbreak/questionnaire_outbreak.json")
//        val questionnaire =
//            iParser.parseResource(Questionnaire::class.java, questionnaireString) as Questionnaire
//
//        val questionnaireResponseString =
//            readFileFromResourcesAsString("/measles-outbreak/questionnaire_response_outbreak.json")
//
//        val questionnaireResponse =
//            iParser.parseResource(QuestionnaireResponse::class.java, questionnaireResponseString)
//                    as QuestionnaireResponse
//        val structureMap =
//            readFileFromResourcesAsString("/measles-outbreak/MeaslesQuestionnaireToResources.map")
//
//        val measlesOutbreakPackage =
//            NpmPackage.fromPackage(readFileFromResources("/measles-outbreak/package.r4.tgz"))
//        val basePackage = NpmPackage.fromPackage(readFileFromResources("/measles-outbreak/package.tgz"))
//
//        val workerContext = helper.loadWorkerContext(measlesOutbreakPackage, basePackage)
//        val transformSupportServices = R4TransformSupportServicesLM(workerContext, mutableListOf())
//
//        val bundle =
//            helper.extract(
//                questionnaire,
//                questionnaireResponse,
//                R4StructureMapExtractionContext(
//                    transformSupportServices,
//                    workerContext = workerContext,
//                ) { _, worker ->
//                    StructureMapUtilities(worker).parse(structureMap, "MeaslesQuestionnaireToResources")
//                },
//            )
//
//        assertNotNull("Bundle object should not be null", bundle);
//        assertTrue("Bundle entry should not be empty", bundle.entry.isNotEmpty())
//
//
//        val patient = bundle.entry.find { it.resource.resourceType == ResourceType.Patient }?.resource as Patient?
//        assertNotNull("Patient should not be null", patient)
//
//        assertEquals("John Doe", patient?.name?.first()?.family)
//    }
//
//    private fun readFileFromResourcesAsString(filename: String) =
//        readFileFromResources(filename).bufferedReader().use { it.readText() }
//
//    private fun readFileFromResources(filename: String) = javaClass.getResourceAsStream(filename)!!
//
//}