package ca.uhn.fhir.jpa.starter.customOperations.r4.r4mapping

import org.hl7.fhir.r4.model.CanonicalType
import org.hl7.fhir.r4.model.Questionnaire

/**
 * See
 * [Extension: target structure map](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-targetStructureMap.html)
 * .
 */
private const val TARGET_STRUCTURE_MAP: String =
    "http://hl7.org/fhir/uv/sdc/StructureDefinition/sdc-questionnaire-targetStructureMap"

/**
 * The StructureMap url in the
 * [target structure-map extension](http://build.fhir.org/ig/HL7/sdc/StructureDefinition-sdc-questionnaire-targetStructureMap.html)
 * s.
 */

val Questionnaire.targetStructureMap: String?
    get() {
        val extensionValue =
            this.extension.singleOrNull { it.url == TARGET_STRUCTURE_MAP }?.value ?: return null
        return if (extensionValue is CanonicalType) extensionValue.valueAsString else null
    }

