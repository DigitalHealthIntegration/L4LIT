//package ca.uhn.fhir.jpa.starter.customOperations
//
//import org.springframework.http.ResponseEntity
//import org.springframework.web.multipart.MultipartFile
//
//interface FhirOperationStrategy {
//    suspend fun apply(patientId: String, planDefinitionId: String): ResponseEntity<String>
//    suspend fun extract(mapperFile: MultipartFile?, files: List<MultipartFile>): ResponseEntity<String>
//    suspend fun evaluate(patientResource: List<MultipartFile>, measureId: String, start: String, end: String, reportType: String, subjectId: String, practitioner: String): ResponseEntity<String>
//
//}
