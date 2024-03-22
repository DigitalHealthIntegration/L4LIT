# HAPI FHIR JPA Server Starter

This project extends the HAPI FHIR JPA Server Starter and adds additional FHIR operations.


## Loading Implementation Guide

You can load Implementation Guides into the HAPI FHIR JPA Server Starter through the `application.yaml` file. Below is the format for specifying Implementation Guides in the `application.yaml` file:

```yaml
implementationguides:
  smart:
    packageUrl: https://worldhealthorganization.github.io/smart-immunizations-measles/package.tgz
    name: smart.who.int.immunizations-measles
    version: 0.1.0
    reloadExisting: false
    installMode: STORE_AND_INSTALL
```



## Getting Started with Docker: Starting Your Application

To start the application, run the following command in your terminal:

```bash
docker-compose up --build -d
```

## Additional FHIR Operations

### $apply

#### **Prerequisites :**

Before using the `$apply` operation, make sure to POST the following example resources to the HAPI FHIR server:

- **Patient**: POST a Patient resource with ID `P123456`.
- **PlanDefinition**: POST a PlanDefinition resource with ID `IMMZDTUmbrella`.

#### Operation Details

- **URL**: `http://localhost:8080/fhir/PlanDefinition/{id}/$apply`
- **Parameters**:
  - `subject` (string): The ID of the subject (e.g., Patient/P123456).

### $extract

When making a request to the `$extract` operation, ensure that the corresponding QuestionnaireResponse resource is included in the body section of the request.

#### Operation Details

- **URL**: `http://localhost:8080/fhir/QuestionnaireResponse/{id}/$extract`


### $evaluate-measure

#### **Prerequisites :**

Before using the `$evaluate-measure` operation, make sure to POST the following example resources to the HAPI FHIR server:

- **Patient**: POST a Patient resource with ID `HTN1-patient-1`.
- **Measure**: POST a Measure resource with ID `HTN1Measure`.
- **Library**: POST a Library resource with ID `HTN1`.

#### Operation Details

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
