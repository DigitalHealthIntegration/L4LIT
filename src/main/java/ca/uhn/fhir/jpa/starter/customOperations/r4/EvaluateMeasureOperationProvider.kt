package ca.uhn.fhir.jpa.starter.customOperations.r4

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.Operation
import ca.uhn.fhir.rest.annotation.OperationParam
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r4.model.Measure
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.OperationOutcome
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class EvaluateMeasureOperationProvider {

    @Autowired
    private lateinit var r4FhirOperationHelper: R4FhirOperationHelper

    private val logger = LoggerFactory.getLogger(ExtractOperationProvider::class.java)

    @Operation(name = "\$evaluate-measure", idempotent = true, global = true, type = Measure::class)
    fun evaluateMeasure(
        @IdParam theId: IdType,
        @OperationParam(name = "start", min = 1, max = 1) start: String?,
        @OperationParam(name = "end", min = 1, max = 1) end: String?,
        @OperationParam(name = "reportType", min = 1, max = 1) reportType: String?,
        @OperationParam(name = "subjectId", min = 1, max = 1) subjectId: String?,
        @OperationParam(name = "practitioner", min = 1, max = 1) practitioner: String?,
    ): IBaseResource {
        return try {
            val measureId = theId.idPart
            val subjectIdOrNull: String? = subjectId?.takeIf { it != "null" }
            val practitionerIdOrNull: String? = practitioner?.takeIf { it != "null" }
            r4FhirOperationHelper.evaluateMeasure(measureId, start, end, reportType, subjectIdOrNull, practitionerIdOrNull)
        }  catch (e: Exception) {
            handleException(e)
        }
    }

    private fun handleException(exception: Exception): OperationOutcome {
        val message = when (exception) {
            is IllegalArgumentException -> "Validation failed: ${exception.message}"
            else -> "An unexpected error occurred: ${exception.message}"
        }
        logger.error(message)
        return r4FhirOperationHelper.buildOperationOutcome(message)
    }


}