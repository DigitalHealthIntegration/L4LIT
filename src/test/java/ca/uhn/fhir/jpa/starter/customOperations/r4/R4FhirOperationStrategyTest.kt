package ca.uhn.fhir.jpa.starter.customOperations.r4

import ca.uhn.fhir.jpa.starter.customOperations.services.HelperService
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.springframework.mock.web.MockMultipartFile
import java.io.FileNotFoundException

class R4FhirOperationStrategyTest {
    private val helperTest = R4FhirOperationHelperTest()
    private val helperService = HelperService()

    private val helper = R4FhirOperationHelper()

    // Set up the tested object
    private val strategy = R4FhirOperationStrategy()
    init {
        // Set up the tested object
        strategy.helper = helper
        strategy.helperService = helperService
    }

    @Test
     fun `test apply`() = runBlocking {

        // Prepare test data
        val patientId = "Patient-Example"
        val planDefinitionId = "MedRequest-Example"
        val multipartFile = createMockMultipartFile("Patient-Example", "application/json", "/r4/patient/Patient-Example.json")
        val response = strategy.apply(patientId, planDefinitionId, multipartFile)
        val jsonResponse = helperTest.readResourceAsString("/med_request_careplan.json")
        assertEquals(response.body?.let { helperTest.normalizeJson(it) },
            jsonResponse?.let { helperTest.normalizeJson(it) })
    }


     fun createMockMultipartFile(fileName: String, contentType: String, path: String): MockMultipartFile {
        val inputStream = this.javaClass.getResourceAsStream(path)
            ?: throw FileNotFoundException("File not found in resources")

        val content = inputStream.readBytes()
        return MockMultipartFile(fileName, fileName, contentType, content)
    }



    @Test
    fun `test extract`() = runBlocking {
        val questionnaire = createMockMultipartFile("questionnaire_outbreak", "application/json", "/measles-outbreak/questionnaire_outbreak.json")
        val questionnaireResponse = createMockMultipartFile("questionnaire_response_outbreak", "application/json", "/measles-outbreak/questionnaire_response_outbreak.json")

        val files = listOf(questionnaire, questionnaireResponse)
        val mapperFile = createMockMultipartFile("MeaslesQuestionnaireToResources", "text/plain", "/measles-outbreak/MeaslesQuestionnaireToResources.map")

        val response = strategy.extract(mapperFile, files)
        val jsonBody = response.body
        assertNotNull(jsonBody) { "JSON body is null" }
    }

}