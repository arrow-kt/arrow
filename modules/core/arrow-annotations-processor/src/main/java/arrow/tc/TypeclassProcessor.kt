package arrow.tc

import arrow.common.utils.AbstractProcessor
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.knownError
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.serialization.deserialization.TypeTable
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class TypeclassesProcessor : AbstractProcessor() {

    private val annotatedList: MutableList<AnnotatedTypeclass> = mutableListOf<AnnotatedTypeclass>()

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(typeClassAnnotationClass.canonicalName)

    /**
     * Processor entry point
     */
    override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
        annotatedList += roundEnv
                .getElementsAnnotatedWith(typeClassAnnotationClass)
                .map { element ->
                    when (element.kind) {
                        ElementKind.CLASS -> processClass(element as TypeElement)
                        ElementKind.INTERFACE -> processClass(element as TypeElement)
                        else -> knownError("${typeClassAnnotationName}AnnotationName can only be used on classes")
                    }
                }

        if (roundEnv.processingOver()) {
            val generatedDir = File(this.generatedDir!!, typeClassAnnotationClass.simpleName).also { it.mkdirs() }
            TypeclassFileGenerator(generatedDir, annotatedList).generate()
        }
    }

    private fun processClass(element: TypeElement): AnnotatedTypeclass {
        val proto = getClassOrPackageDataWrapper(element) as ClassOrPackageDataWrapper.Class
        val typeTable = TypeTable(proto.classProto.typeTable)
        val superTypes: List<ClassOrPackageDataWrapper.Class> =
                recurseTypeclassInterfaces(proto, typeTable, emptyList()).map { it as ClassOrPackageDataWrapper.Class }
        val syntax = element.annotationMirrors.flatMap { am ->
            am.elementValues.entries.filter {
                "syntax" == it.key.simpleName.toString()
            }.map { it.value.toString().toBoolean() }
        }.firstOrNull() ?: true
        return AnnotatedTypeclass(element, proto, superTypes, syntax)
    }

}
