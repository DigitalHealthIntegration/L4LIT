package ca.uhn.fhir.jpa.starter.customOperations.r4

import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.elementmodel.Manager
import org.hl7.fhir.r4.model.Base
import org.hl7.fhir.r4.model.Coding
import org.hl7.fhir.r4.model.ResourceFactory
import org.hl7.fhir.r4.model.Enumerations
import org.hl7.fhir.r4.model.StructureDefinition
import org.hl7.fhir.r4.model.Resource
import org.hl7.fhir.r4.terminologies.ConceptMapEngine
import org.hl7.fhir.r4.utils.StructureMapUtilities



class R4TransformSupportServicesLM(
    private val workerContext: IWorkerContext,
    private val outputs: MutableList<Base>,
) : StructureMapUtilities.ITransformerServices {

    override fun createType(appInfo: Any, name: String): Base {
        return try {
            ResourceFactory.createResourceOrType(name)
        } catch (fhirException: FHIRException) {
            Manager.build(
                workerContext,
                workerContext.fetchResource(
                    StructureDefinition::class.java,
                    name,
                ),
            )
        }
    }

    override fun createResource(appInfo: Any, res: Base, atRootofTransform: Boolean): Base {
        if (atRootofTransform) outputs.add(res)
        return try {
            val fhirType = Enumerations.FHIRAllTypes.fromCode(res.fhirType())
            val constructor =
                Class.forName(
                    "org.hl7.fhir.r4.model." + fhirType.display,
                )
                    .getConstructor()
            constructor.newInstance() as Base
        } catch (e: Exception) {
            res
        }
    }

    override fun translate(appInfo: Any, source: Coding, conceptMapUrl: String): Coding? {
        val conceptMapEngine = ConceptMapEngine(workerContext as SimpleWorkerContext)
        return conceptMapEngine.translate(source, conceptMapUrl)
    }

    override fun resolveReference(
        appContext: Any,
        url: String,
    ): Base {
        return workerContext.fetchResource(
            Resource::class.java,
            url,
        )
    }

    @Throws(FHIRException::class)
    override fun performSearch(appContext: Any, url: String): List<Base> {
        throw FHIRException("performSearch is not supported yet")
    }

    override fun log(message: String) {}
}