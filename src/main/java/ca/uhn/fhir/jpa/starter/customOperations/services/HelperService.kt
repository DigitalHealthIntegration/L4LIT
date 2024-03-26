package ca.uhn.fhir.jpa.starter.customOperations.services

import org.hl7.fhir.utilities.npm.NpmPackage
import org.springframework.stereotype.Service

@Service
class HelperService {

    lateinit var installedNpmPackage: NpmPackage
    fun readFileFromResources(filename: String) = javaClass.getResourceAsStream(filename)!!

}