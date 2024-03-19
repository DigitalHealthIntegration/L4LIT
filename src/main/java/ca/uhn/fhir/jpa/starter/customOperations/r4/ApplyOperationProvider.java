package ca.uhn.fhir.jpa.starter.customOperations.r4;

import ca.uhn.fhir.jpa.rp.r4.PlanDefinitionResourceProvider;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Service;

@Service
public class ApplyOperationProvider {

	R4FhirOperationHelper helper = new R4FhirOperationHelper();

	// Define an instance-level operation method
	@Operation(name = "$apply", idempotent = true, global = true, type = PlanDefinition.class)
	public IBaseResource apply(
		@IdParam IdType theId,
		@OperationParam(name = "subject", min = 1, max = 1) String subject) {

		// Retrieve the ID of the patient from theId parameter

		String planDefinitionId = theId.getIdPart();
		String patientId = extractPatientId(subject);


		return helper.generateCarePlan(
			patientId,
			theId.toString(),
			null, // encounterId
			null, // practitionerId
			null, // organizationId
			null, // userType
			null, // userLanguage
			null, // userTaskContext
			null, // setting
			null // settingContext
		);
	}

	private String extractPatientId(String subject) {
		// Split the subject parameter value by "/" and get the last part
		String[] parts = StringUtils.split(subject, '/');
		if (parts != null && parts.length > 0) {
			return parts[parts.length - 1]; // Last part is the patient ID
		}
		return null; // If subject parameter format is invalid
	}


}



