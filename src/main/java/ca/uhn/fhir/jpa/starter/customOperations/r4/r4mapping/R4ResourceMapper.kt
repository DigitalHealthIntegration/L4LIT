package ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping

import org.hl7.fhir.r4.model.Questionnaire
import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.QuestionnaireResponse
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.utils.StructureMapUtilities


object R4ResourceMapper {

    private const val TARGET_STRUCTURE_MAP: String =
        "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap"

    private val Questionnaire.targetStructureMap: String?
        get() {
            val extensionValue =
                this.extension.singleOrNull { it.url == R4ResourceMapper.TARGET_STRUCTURE_MAP }?.value ?: return null
            return if (extensionValue is CanonicalType) extensionValue.valueAsString else null
        }

    /**
     * Extracts FHIR resources from [questionnaireResponse] (as response to [questionnaire]) using
     * StructureMap-based extraction.
     *
     * @param structureMapProvider provides the referenced [StructureMap] either from persistence or a
     *   remote service.
     * @return a [Bundle] including the extraction results, or `null` if [structureMapProvider] is
     *   missing.
     *
     * See http://build.fhir.org/ig/HL7/sdc/extraction.html#structuremap-based-extraction for more on
     * StructureMap-based extraction.
     */
    suspend fun extractByStructureMap(
        questionnaire: Questionnaire,
        questionnaireResponse: QuestionnaireResponse,
        r4StructureMapExtractionContext: R4StructureMapExtractionContext,
    ): Bundle {
        val structureMapProvider = r4StructureMapExtractionContext.structureMapProvider
        val simpleWorkerContext =
            r4StructureMapExtractionContext.workerContext.apply { setExpansionProfile(Parameters()) }
        val structureMap = structureMapProvider(questionnaire.targetStructureMap!!, simpleWorkerContext)

        return Bundle().apply {
            StructureMapUtilities(
                simpleWorkerContext,
                r4StructureMapExtractionContext.transformSupportServices,
            )
                .transform(simpleWorkerContext, questionnaireResponse, structureMap, this)
        }
    }

}