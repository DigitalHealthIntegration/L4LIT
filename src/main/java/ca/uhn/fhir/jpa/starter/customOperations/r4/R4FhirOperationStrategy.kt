//package ca.uhn.fhir.jpa.starter.customOperations.r4
//
//import ca.uhn.fhir.context.FhirContext
//import ca.uhn.fhir.context.FhirVersionEnum
//import ca.uhn.fhir.jpa.starter.customOperations.FhirOperationStrategy
//import ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping.R4StructureMapExtractionContext
//import ca.uhn.fhir.jpa.starter.customOperations.services.HelperService
//import org.hl7.fhir.r4.model.CanonicalType
//import org.hl7.fhir.r4.model.Questionnaire
//import org.hl7.fhir.r4.model.QuestionnaireResponse
//import org.hl7.fhir.r4.utils.StructureMapUtilities
//import org.hl7.fhir.utilities.npm.NpmPackage
//import org.opencds.cqf.fhir.utility.monad.Eithers
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.multipart.MultipartFile
//import java.io.IOException
////import org.opencds.cqf.fhir.cr.measure.common
//class R4FhirOperationStrategy : FhirOperationStrategy {
//    var helper: R4FhirOperationHelper = R4FhirOperationHelper()
//    var helperService = HelperService()
//
//    override suspend fun apply(patientId: String, planDefinitionId: String): ResponseEntity<String> {
//        // Call helper functions specific to R4
//
//        return try {
////            val fileCreated = helperService.createFiles(file)
////            if (fileCreated) {
////                // File creation succeeded, proceed with generating the care plan
////                val output =
////                    helper.generateCarePlan(subject = "Patient/$patientId", planDefinitionId = planDefinitionId)
////                // Convert output to JSON
////                val jsonBundle =
////                    FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().encodeResourceToString(output)
////                ResponseEntity.ok().body(jsonBundle)
////            } else {
////                // File creation failed, return an error response
////                ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
////                    .body("Failed to create patient resource json file")
////            }
//
//            // File creation succeeded, proceed with generating the care plan
//            val output =
//                helper.generateCarePlan(subject = "Patient/$patientId", planDefinitionId = planDefinitionId)
//            // Convert output to JSON
//            val jsonBundle =
//                FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().encodeResourceToString(output)
//            ResponseEntity.ok().body(jsonBundle)
//
//        } catch (e: IOException) {
//            // Handle file-related IO exceptions
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("An error occurred during file operations: ${e.message}")
//        } catch (e: IllegalArgumentException) {
//            // Handle invalid parameter exceptions
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters: ${e.message}")
//        } catch (e: Exception) {
//            // Handle other unexpected exceptions
//            e.printStackTrace()
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("An unexpected error occurred: ${e.message}")
//        }
////        finally {
////            // Always delete the file after processing
////            helperService.deleteFileInResourceFolder("${file.originalFilename}")
////        }
//
//
//    }
//
//
//    override suspend fun extract(mapperFile: MultipartFile?, files: List<MultipartFile>): ResponseEntity<String> {
//        // Implement extract logic for FHIR version R4
//        var mapping: String = "";
//        var questionnaire: Questionnaire? = null
//        var questionnaireResponse: QuestionnaireResponse? = null
//        files.forEach { file ->
//            val (receivedQuestionnaire, receivedQuestionnaireResponse) = helperService.readResourceFile(file)
//            questionnaire = questionnaire ?: receivedQuestionnaire
//            questionnaireResponse = questionnaireResponse ?: receivedQuestionnaireResponse
//        }
//
//        mapperFile?.let {
//            // Read content of additional file if it's provided
//            mapping = it.inputStream.bufferedReader().use { reader -> reader.readText() }
//        }
//
//        val measlesOutbreakPackage =
//            NpmPackage.fromPackage(helperService.readFileFromResources("/measles-outbreak/package.r4.tgz"))
//        val basePackage = NpmPackage.fromPackage(helperService.readFileFromResources("/measles/package.tgz"))
//        val workerContext = helper.loadWorkerContext(measlesOutbreakPackage, basePackage)
//        val transformSupportServices = R4TransformSupportServicesLM(workerContext, mutableListOf())
//
//        val bundle = questionnaire?.let {
//            questionnaireResponse?.let { it1 ->
//                helper.extract(
//                    it, it1, R4StructureMapExtractionContext(transformSupportServices, workerContext) { _, worker ->
//                        StructureMapUtilities(worker).parse(mapping, "MeaslesQuestionnaireToResources")
//                    }
//                )
//            }
//        }
//
//        val jsonBundle = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().encodeResourceToString(bundle)
//        return ResponseEntity.ok().body(jsonBundle)
//
//    }
//
//    override suspend fun evaluate(patientResource: List<MultipartFile>, measureId: String, start: String, end: String, reportType: String, subjectId: String, practitioner: String): ResponseEntity<String> {
////        return try {
////            val fileCreated = helperService.createFiles(patientResource)
////            if (fileCreated) {
////                // File creation succeeded, proceed with generating the care plan
////                val output = helper.evaluateMeasure(measureId, start, end, reportType,subjectId,practitioner)
////                // Convert output to JSON
////                val jsonBundle =
////                    FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().encodeResourceToString(output)
////                ResponseEntity.ok().body(jsonBundle)
////            } else {
////                // File creation failed, return an error response
////                ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
////                    .body("Failed to create patient resource json file")
////            }
////        } catch (e: IOException) {
////            // Handle file-related IO exceptions
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                .body("An error occurred during file operations: ${e.message}")
////        } catch (e: IllegalArgumentException) {
////            // Handle invalid parameter exceptions
////            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters: ${e.message}")
////        } catch (e: Exception) {
////            e.printStackTrace()
////            // Handle other unexpected exceptions
////            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
////                .body("An unexpected error occurred: ${e.message}")
////        } finally {
////            // Always delete the file after processing
////            patientResource.mapNotNull { it.originalFilename }.let(helperService::deleteFilesInResourceFolder)
////        }
//        return ResponseEntity.ok().body("jsonBundle")
//    }
//
//}