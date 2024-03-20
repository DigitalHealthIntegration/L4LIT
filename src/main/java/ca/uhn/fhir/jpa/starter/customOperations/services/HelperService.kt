package ca.uhn.fhir.jpa.starter.customOperations.services

import ca.uhn.fhir.context.FhirContext
//import ca.uhn.fhir.jpa.starter.customOperations.FhirOperationStrategy
//import ca.uhn.fhir.jpa.starter.customOperations.r4.R4FhirOperationStrategy
import ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping.R4ResourceMapper
//import ca.uhn.fhir.jpa.starter.customOperations.r5.R5FhirOperationStrategy
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.utilities.npm.NpmPackage
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
//
@Service
class HelperService {

    lateinit var installedNpmPackage: NpmPackage


    //    private val fhirContextR4 = FhirContext.forR4Cached()
////    private val evaluationSettings: EvaluationSettings = EvaluationSettings.getDefault()
//    private val resource = ClassPathResource("r4")
//    private val directoryPath: Path = Paths.get(resource.uri)
////    private val igRepository = IgRepository(fhirContextR4, directoryPath)
////    private val planDefinitionProcessor = PlanDefinitionProcessor(igRepository, evaluationSettings)
//
//
//    private val logger = LoggerFactory.getLogger(HelperService::class.java)
//
//
//
//
//
//
////    fun generateCarePlan(
////        subject: String,
////        planDefinitionId: String,
////        encounterId: String? = null,
////        practitionerId: String? = null,
////        organizationId: String? = null,
////        userType: IBaseDatatype? = null,
////        userLanguage: IBaseDatatype? = null,
////        userTaskContext: IBaseDatatype? = null,
////        setting: IBaseDatatype? = null,
////        settingContext: IBaseDatatype? = null
////    ): IBaseResource {
////        return planDefinitionProcessor.apply(
////            Eithers.forMiddle3(Ids.newId(igRepository.fhirContext(), ResourceType.PlanDefinition.name, planDefinitionId)),
////            /* subject = */ subject,
////            /* encounterId = */ encounterId,
////            /* practitionerId = */ practitionerId,
////            /* organizationId = */ organizationId,
////            /* userType = */ userType,
////            /* userLanguage = */ userLanguage,
////            /* userTaskContext = */ userTaskContext,
////            /* setting = */ setting,
////            /* settingContext = */ settingContext
////        ) as IBaseResource
////    }
//
    fun readFileFromResources(filename: String) = javaClass.getResourceAsStream(filename)!!
//
//    fun readResourceFile(file: MultipartFile): Pair<Questionnaire?, QuestionnaireResponse?> {
//        val inputStream = file.inputStream
//        val reader = BufferedReader(InputStreamReader(inputStream))
//        val parser = fhirContextR4.newJsonParser()
//
//        return when (val resource = parser.parseResource(reader.use { it.readText() })) {
//            is Questionnaire -> Pair(resource, null)
//            is QuestionnaireResponse -> Pair(null, resource)
//            else -> Pair(null, null)
//        }
//    }
//
////    suspend fun extract(
////        questionnaire: Questionnaire,
////        questionnaireResponse: QuestionnaireResponse,
////        structureMapExtractionContext: StructureMapExtractionContext? = null,
////        profileLoader: ProfileLoader? = null,
////    ): Bundle {
////        return when {
////            questionnaire.targetStructureMap == null ->
////                ResourceMapper.extractByDefinition(
////                    questionnaire,
////                    questionnaireResponse,
////                    object : ProfileLoader {
////                        // Mutable map of key-canonical url as string for profile and
////                        // value-StructureDefinition of resource claims to conforms to.
////                        val structureDefinitionMap: MutableMap<String, StructureDefinition?> = hashMapOf()
////
////                        override fun loadProfile(url: CanonicalType): StructureDefinition? {
////                            if (profileLoader == null) {
////                                logger.info(
////                                    "ProfileLoader implementation required to load StructureDefinition that this resource claims to conform to",
////                                )
////                                return null
////                            }
////                            structureDefinitionMap[url.toString()]?.also {
////                                return it
////                            }
////                            return profileLoader.loadProfile(url).also {
////                                structureDefinitionMap[url.toString()] = it
////                            }
////                        }
////                    },
////                )
////            structureMapExtractionContext != null -> {
////                ResourceMapper.extractByStructureMap(
////                    questionnaire,
////                    questionnaireResponse,
////                    structureMapExtractionContext
////                )
////            }
////            else -> {
////                Bundle()
////            }
////        }
////    }
//
//    fun createFiles(file: MultipartFile): Boolean {
//        return createFile(file)
//    }
//
//    fun createFiles(files: List<MultipartFile>): Boolean {
//        return files.all { createFile(it) }
//    }
//
//    private fun createFile(file:MultipartFile): Boolean{
//        return try {
//            val fileName = file.originalFilename
//            val filePath = directoryPath.resolve(fileName)
//            Files.copy(file.inputStream, filePath)
//            true // Return true if file creation succeeds
//        }  catch (e: IOException) {
//            // Handle the exception appropriately, for example, log the error or notify the user
//            logger.error("Error occurred while creating file", e)
//            false // Return false if file creation fails
//        }
//    }
//
//    fun deleteFileInResourceFolder(fileName: String) {
//        if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
//            Files.newDirectoryStream(directoryPath).use { directoryStream ->
//                for (filePath in directoryStream) {
//                    if (Files.isRegularFile(filePath) && filePath.fileName.toString() == fileName) {
//                        Files.delete(filePath)
//                        logger.info("Deleted file: $filePath")
//                        return  // Exit the function after deleting the file
//                    }
//                }
//            }
//            logger.info("File '$fileName' not found in the input folder.")
//        } else {
//            logger.info("Input folder 'r4' does not exist or is not a directory.")
//        }
//    }
//
//    fun deleteFilesInResourceFolder(fileNames: List<String>) {
//        for (fileName in fileNames) {
//            deleteFileInResourceFolder(fileName)
//        }
//    }
//
//
//
//
//
//    // Helper method to get the appropriate FhirOperationStrategy
//    fun getStrategy(fhirVersion: String): FhirOperationStrategy {
//        return when (fhirVersion) {
//            FhirVersion.R4.code() -> R4FhirOperationStrategy()
//            FhirVersion.R5.code() -> R5FhirOperationStrategy()
//            else -> throw IllegalArgumentException("Unsupported FHIR version: $fhirVersion")
//        }
//    }
//
//    enum class FhirVersion {
//        R4,
//        R5;
//
//        fun code(): String {
//            return this.name.toLowerCase()
//        }
//    }
//
//
}