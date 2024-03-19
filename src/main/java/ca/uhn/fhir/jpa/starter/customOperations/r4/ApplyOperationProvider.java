package ca.uhn.fhir.jpa.starter.customOperations.r4;

import ca.uhn.fhir.jpa.rp.r4.PlanDefinitionResourceProvider;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.PlanDefinition;
import org.hl7.fhir.r4.model.StringType;
import org.springframework.stereotype.Service;

@Service
public class ApplyOperationProvider extends PlanDefinitionResourceProvider implements IResourceProvider {

	// Define an instance-level operation method
	@Operation(name = "$apply", idempotent = true, global = true)
	public Patient apply(
		@IdParam IdType theId,
		@OperationParam(name = "subject", min = 1, max = 1) String subject) {

		// Retrieve the ID of the patient from theId parameter
		// Retrieve the ID of the patient from theId parameter
		String planDefinitionId = theId.getIdPart();
		String patientId = extractPatientId(subject);

		System.out.println("id is "+ planDefinitionId);
		System.out.println("planDefinition is "+ patientId);


		// Perform your operation logic using the patient ID and custom parameter value
		// For example:
		Patient patient = retrievePatientById(patientId);

		return patient;
	}

	private String extractPatientId(String subject) {
		// Split the subject parameter value by "/" and get the last part
		String[] parts = StringUtils.split(subject, '/');
		if (parts != null && parts.length > 0) {
			return parts[parts.length - 1]; // Last part is the patient ID
		}
		return null; // If subject parameter format is invalid
	}

	private Patient retrievePatientById(String patientId) {
		// Implement logic to retrieve the patient from your data source
		// This is just a placeholder method
		// Create a new Patient instance
		Patient patient = new Patient();

		// Set the ID for the patient
		patient.setId(patientId);

		// Return the patient
		return patient;
	}


}

	// Example method to retrieve a patient by ID


//	// Example method to perform a custom operation
//	private String performCustomOperation(Patient patient, String parameterValue) {
//		// Implement your custom operation logic here
//		// This is just a placeholder method
//		return "Operation performed on patient: " + patient.getIdElement().getIdPart() + ", Parameter value: " + parameterValue;
//	}
//}

