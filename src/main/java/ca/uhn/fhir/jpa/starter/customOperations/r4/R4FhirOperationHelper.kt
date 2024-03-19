package ca.uhn.fhir.jpa.starter.customOperations.r4

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping.R4ResourceMapper
import ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping.R4StructureMapExtractionContext
//import ca.uhn.fhir.jpa.starter.customOperations.services.HelperService
import org.hl7.fhir.instance.model.api.IBaseBundle
import org.hl7.fhir.instance.model.api.IBaseDatatype
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.*
import org.hl7.fhir.utilities.npm.NpmPackage
import org.opencds.cqf.fhir.cql.EvaluationSettings
import org.opencds.cqf.fhir.cr.measure.MeasureEvaluationOptions
import org.opencds.cqf.fhir.cr.measure.r4.R4MeasureProcessor
import org.opencds.cqf.fhir.cr.plandefinition.PlanDefinitionProcessor
import org.opencds.cqf.fhir.utility.Ids
import org.opencds.cqf.fhir.utility.monad.Eithers
import org.opencds.cqf.fhir.utility.repository.Repositories
import org.opencds.cqf.fhir.utility.repository.ig.IgRepository
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import java.nio.file.Path
import java.nio.file.Paths
open class R4FhirOperationHelper {
    private val fhirContextR4 = FhirContext.forR4Cached()
    private val evaluationSettings: EvaluationSettings = EvaluationSettings.getDefault()


     fun generateCarePlan(
        subject: String,
        planDefinitionId: String,
        encounterId: String? = null,
        practitionerId: String? = null,
        organizationId: String? = null,
        userType: IBaseDatatype? = null,
        userLanguage: IBaseDatatype? = null,
        userTaskContext: IBaseDatatype? = null,
        setting: IBaseDatatype? = null,
        settingContext: IBaseDatatype? = null
    ): IBaseResource {
         val endpoint = Endpoint().apply {
             address = "http://localhost:8080/fhir"
         }

         val restRepository = Repositories.createRestRepository(fhirContextR4,endpoint)
         val planDefinitionProcessor = PlanDefinitionProcessor(restRepository, evaluationSettings)

        return planDefinitionProcessor.apply(
            Eithers.forMiddle3(Ids.newId(fhirContextR4, ResourceType.PlanDefinition.name, planDefinitionId)),
            /* subject = */ subject,
            /* encounterId = */ encounterId,
            /* practitionerId = */ practitionerId,
            /* organizationId = */ organizationId,
            /* userType = */ userType,
            /* userLanguage = */ userLanguage,
            /* userTaskContext = */ userTaskContext,
            /* setting = */ setting,
            /* settingContext = */ settingContext
        ) as IBaseResource
    }

//
//    suspend fun extract(
//        questionnaire: Questionnaire,
//        questionnaireResponse: QuestionnaireResponse,
//        r4StructureMapExtractionContext: R4StructureMapExtractionContext? = null
//    ): Bundle {
//        return when {
//            r4StructureMapExtractionContext != null -> {
//                R4ResourceMapper.extractByStructureMap(
//                    questionnaire,
//                    questionnaireResponse,
//                    r4StructureMapExtractionContext
//                )
//            }
//            else -> {
//                Bundle()
//            }
//        }
//    }
//
//    suspend fun loadWorkerContext(
//        vararg npmPackages: NpmPackage,
//        allowLoadingDuplicates: Boolean = true,
//        loader: SimpleWorkerContext.IContextResourceLoader? = null,
//    ): IWorkerContext {
//        val simpleWorkerContext = SimpleWorkerContext()
//        simpleWorkerContext.apply {
//            isAllowLoadingDuplicates = allowLoadingDuplicates
//            npmPackages.forEach { npmPackage -> loadFromPackage(npmPackage, loader) }
//        }
//        return simpleWorkerContext
//    }
//
////    fun evaluateMeasure(
////        measureId: String,
////        start: String,
////        end: String,
////        reportType: String,
////        subjectId: String? = null,
////        practitioner: String? = null,
////        additionalData: IBaseBundle? = null,
////    ): MeasureReport {
////
////        val subject =
////            if (!subjectId.isNullOrBlank()) {
////                checkAndAddType(subjectId, "Patient")
////            } else if (!practitioner.isNullOrBlank()) {
////                checkAndAddType(practitioner, "Practitioner")
////            } else {
////                // List of null is required to run population-level measures
////                null
////            }
////
////        val report =
////            measureProcessor.evaluateMeasure(
////                /* measure = */ Eithers.forMiddle3<CanonicalType, IdType, Measure>(IdType("Measure", measureId)),
////                /* periodStart = */ start,
////                /* periodEnd = */ end,
////                /* reportType = */ reportType,
////                /* subjectIds = */ listOf(subject),
////                /* additionalData = */ additionalData,
////               null
////            )
////
////        // add subject reference for non-individual reportTypes
////        if (report.type.name == "SUMMARY" && !subject.isNullOrBlank()) {
////            report.subject = Reference(subject)
////        }
////        return report
////    }
//
//    /** Checks if the Resource ID contains a type and if not, adds a default type */
//    private fun checkAndAddType(id: String, defaultType: String): String {
//        return if (id.indexOf("/") == -1) "$defaultType/$id" else id
//    }
//
//
//
}