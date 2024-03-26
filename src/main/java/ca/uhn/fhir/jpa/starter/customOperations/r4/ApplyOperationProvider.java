package ca.uhn.fhir.jpa.starter.customOperations.r4;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplyOperationProvider {

	@Autowired
	private R4FhirOperationHelper helper;

	private static final Logger logger = LoggerFactory.getLogger(ApplyOperationProvider.class);

	@Operation(name = "$apply", idempotent = true, global = true, type = PlanDefinition.class)
	public IBaseResource apply(
		@IdParam IdType theId,
		@OperationParam(name = "subject", min = 1, max = 1) String subject) {

		try {
			String patientId = extractPatientId(subject);
			// Validate patientId is not null
			if (patientId == null) {
				throw new IllegalArgumentException("Patient ID is required.");
			}

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
		} catch (Exception exception) {
			// Catch any other unexpected exceptions
			return handleException(exception);

		}
	}

	public OperationOutcome handleException(Exception exception) {
		String message;
		if (exception instanceof ResourceNotFoundException) {
			message = "Resource not found: " + exception.getMessage();
		} else {
			message = "An unexpected error occurred: " + exception.getMessage();
		}
		logger.error(message);
		return helper.buildOperationOutcome(message);
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



