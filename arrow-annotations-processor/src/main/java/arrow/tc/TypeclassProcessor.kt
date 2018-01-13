package arrow.tc

import com.google.auto.service.AutoService
import arrow.common.utils.AbstractProcessor
import arrow.common.utils.knownError
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
        val proto = getClassOrPackageDataWrapper(element)
        return AnnotatedTypeclass(element, proto)
    }

}
