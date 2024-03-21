package ca.uhn.fhir.jpa.starter.customOperations.r4

import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.Operation
import ca.uhn.fhir.rest.annotation.OperationParam
import org.hl7.fhir.r4.model.Measure
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.MeasureReport
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
    ): MeasureReport {
        try {
            val measureId = theId.idPart
            return r4FhirOperationHelper.evaluateMeasure(measureId, start, end, reportType, subjectId, practitioner )
        } catch (e: IllegalArgumentException) {
            // Handle validation failure
            logger.error("Validation failed: ${e.message}")
            // Optionally, rethrow or handle differently
        } catch (e: Exception) {
            // Catch any other unexpected exceptions
            logger.error("An unexpected error occurred: ${e.message}")
            // Optionally, rethrow or handle differently
        }
        return MeasureReport()
    }

}