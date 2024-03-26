//package ca.uhn.fhir.jpa.starter.customOperations
//
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.stereotype.Component
////
//@Configuration
//class FhirOperationStrategyConfig {
//
//    @Bean
//    fun fhirOperationStrategies(
//        r4FhirOperationStrategy: FhirOperationStrategy,
//        r5FhirOperationStrategy: FhirOperationStrategy
//    ): Map<String, FhirOperationStrategy> {
//        return mapOf(
//            "r4" to r4FhirOperationStrategy,
//            "r5" to r5FhirOperationStrategy
//            // Add more entries for other FHIR versions as needed
//        )
//    }
//// Define a new bean method to create the fhirOperationStrategies bean
//
//
//}