package ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import org.junit.Assert.*
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.ResourceType
import org.hl7.fhir.r4.utils.StructureMapUtilities
import org.hl7.fhir.utilities.npm.NpmPackage
import org.junit.Test
import ca.uhn.fhir.jpa.starter.customOperations.r4.R4FhirOperationHelper
import ca.uhn.fhir.jpa.starter.customOperations.r4.R4TransformSupportServicesLM

class R4ResourceMapperTest {


//    private fun String.toDateFromFormatYyyyMmDd(): Date? = SimpleDateFormat("yyyy-MM-dd").parse(this)

//    class TransformSupportServices(private val outputs: MutableList<Base>) :
//        StructureMapUtilities.ITransformerServices {
//        override fun log(message: String) {}
//        fun getContext(): org.hl7.fhir.r4.context.SimpleWorkerContext {
//            return org.hl7.fhir.r4.context.SimpleWorkerContext()
//        }
//        @Throws(FHIRException::class)
//        override fun createType(appInfo: Any, name: String): Base {
//            return when (name) {
//                "Immunization_Reaction" -> Immunization.ImmunizationReactionComponent()
//                else -> ResourceFactory.createResourceOrType(name)
//            }
//        }
//        override fun createResource(appInfo: Any, res: Base, atRootofTransform: Boolean): Base {
//            if (atRootofTransform) outputs.add(res)
//            return res
//        }
//        @@ -3090,4 +3149,61 @@
//        throw FHIRException("performSearch is not supported yet")
//    }
}

