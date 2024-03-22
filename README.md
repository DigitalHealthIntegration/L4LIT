# HAPI FHIR JPA Server Starter

This project extends the HAPI FHIR JPA Server Starter and adds additional FHIR operations.

## Additional FHIR Operations

### $apply
- URL: `http://localhost:8080/fhir/PlanDefinition/{id}/$apply`
- Parameters:
    - `subject` (string): The ID of the subject (e.g., Patient/P123456).

### $extract
- URL: `http://localhost:8080/fhir/QuestionnaireResponse/{id}/$extract`

### $evaluate-measure
- URL: `http://localhost:8080/fhir/Measure/{id}/$evaluate-measure`
- Parameters:
    - `start` (string): Start date (e.g., "2020-08-16").
    - `end` (string): End date (e.g., "2022-08-16").
    - `reportType` (string): Type of report.
    - `subjectId` (string): ID of the subject (e.g., "Patient/HTN1-patient-1").
    - `practitioner` (string): ID of the practitioner (optional).

## Usage
To use these operations, you can hit the provided URLs with the appropriate parameters.

- Example:
    - `$apply`: `http://localhost:8080/fhir/PlanDefinition/IMMZDTUmbrella/$apply?subject=Patient/P123456`
    - `$extract`: `http://localhost:8080/fhir/QuestionnaireResponse/Example.IMMZ.C.QuestionnaireResponse.1/$extract`
    - `$evaluate-measure`: `http://localhost:8080/fhir/Measure/HTN1Measure/$evaluate-measure?start=2020-08-16&end=2022-08-16&reportType=subject&subjectId=Patient/HTN1-patient-1&practitioner=null`

Make sure to replace `{id}` in the URLs with the appropriate resource ID.
