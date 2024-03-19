//package ca.uhn.fhir.jpa.starter.customOperations.r5
//
//import ca.uhn.fhir.jpa.starter.customOperations.FhirOperationStrategy
//import org.springframework.http.ResponseEntity
//import org.springframework.web.multipart.MultipartFile
//
//class R5FhirOperationStrategy  : FhirOperationStrategy {
//
//    override suspend fun apply(patientId: String, planDefinitionId: String): ResponseEntity<String> {
//       return ResponseEntity.ok().body("i am inside apply method R5")
//    }
//
//    override suspend fun extract(mapperFile: MultipartFile?, files: List<MultipartFile>): ResponseEntity<String> {
//        return ResponseEntity.ok().body("i am inside apply method R5")
//    }
//    override suspend fun evaluate(patientResource: List<MultipartFile>, measureId: String, start: String, end: String, reportType: String, subjectId: String, practitioner: String): ResponseEntity<String> {
//        return ResponseEntity.ok().body("i am inside apply method R5")
//    }
//}
