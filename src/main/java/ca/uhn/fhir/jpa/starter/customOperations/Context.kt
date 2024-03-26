//package ca.uhn.fhir.jpa.starter.customOperations
//
//import ca.uhn.fhir.jpa.starter.customOperations.FhirOperationStrategy
//import org.springframework.http.ResponseEntity
//import org.springframework.web.multipart.MultipartFile
//
//// Context class
//class Context(private var strategy: FhirOperationStrategy) {
//    fun setStrategy(strategy: FhirOperationStrategy): Context {
//        this.strategy = strategy
//        return this
//    }
//    suspend fun apply(patientId: String, planDefinitionId: String): ResponseEntity<String> {
//        return strategy.apply(patientId, planDefinitionId)
//    }
//    suspend fun extract(mapperFile: MultipartFile?, files: List<MultipartFile>): ResponseEntity<String> {
//        return strategy.extract(mapperFile, files)
//    }
//    suspend fun evaluate(patientResource: List<MultipartFile>, measureId: String, start: String, end: String, reportType: String, subjectId: String, practitioner: String): ResponseEntity<String> {
//        return strategy.evaluate(patientResource, measureId, start, end, reportType, subjectId, practitioner)
//    }
//
//}