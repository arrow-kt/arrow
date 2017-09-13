package kategory.instances

import com.google.auto.service.AutoService
import kategory.common.utils.AbstractProcessor
import kategory.common.utils.ClassOrPackageDataWrapper
import kategory.common.utils.knownError
import org.jetbrains.kotlin.serialization.deserialization.TypeTable
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class InstanceProcessor : AbstractProcessor() {

    private val annotatedList = mutableListOf<AnnotatedInstance>()

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(instanceAnnotationClass.canonicalName)

    /**
     * Processor entry point
     */
    override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
        annotatedList += roundEnv
                .getElementsAnnotatedWith(instanceAnnotationClass)
                .map { element ->
                    when (element.kind) {
                        ElementKind.INTERFACE -> processClass(element as TypeElement)
                        else -> knownError("$instanceAnnotationName can only be used on interfaces")
                    }
                }

        if (roundEnv.processingOver()) {
            val generatedDir = File(this.generatedDir!!, instanceAnnotationClass.simpleName).also { it.mkdirs() }
            InstanceFileGenerator(generatedDir, annotatedList).generate()
        }
    }

    private fun processClass(element: TypeElement): AnnotatedInstance {
        val proto: ClassOrPackageDataWrapper.Class = getClassOrPackageDataWrapper(element) as ClassOrPackageDataWrapper.Class
        val typeTable = TypeTable(proto.classProto.typeTable)
        val superTypes: List<ClassOrPackageDataWrapper> = recurseTypeclassInterfaces(proto, typeTable, emptyList())
        return AnnotatedInstance(element, proto, superTypes, this)
    }

}