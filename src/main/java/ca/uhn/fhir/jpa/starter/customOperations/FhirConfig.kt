//package ca.uhn.fhir.jpa.starter.customOperations
//
//import ca.uhn.fhir.jpa.starter.customOperations.r4.R4FhirOperationStrategy
//import ca.uhn.fhir.jpa.starter.customOperations.r5.R5FhirOperationStrategy
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//
//@Configuration
//class FhirConfig {
//
////    @Bean
////    fun r4FhirOperationStrategy(): FhirOperationStrategy {
////        println("Initializing r4FhirOperationStrategy bean")
////        return R4FhirOperationStrategy()
////    }
////
////    @Bean
////    fun r5FhirOperationStrategy(): FhirOperationStrategy {
////        println("Initializing r5FhirOperationStrategy bean")
////        return R5FhirOperationStrategy()
////    }
//
//    @Bean
//    fun context(@Qualifier("r4FhirOperationStrategy") fhirOperationStrategy: FhirOperationStrategy): Context {
//        return Context(fhirOperationStrategy)
//    }
//
//}