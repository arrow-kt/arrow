package kategory.instances

import com.google.auto.service.AutoService
import kategory.common.utils.AbstractProcessor
import kategory.common.utils.ClassOrPackageDataWrapper
import kategory.common.utils.knownError
import kategory.implicits.implicitAnnotationClass
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

    override fun getSupportedAnnotationTypes(): Set<String> = setOf(implicitAnnotationClass.canonicalName)

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
            val generatedDir = File(this.generatedDir!!, implicitAnnotationClass.simpleName).also { it.mkdirs() }
            InstanceFileGenerator(generatedDir, annotatedList).generate()
        }
    }

    private fun processClass(element: TypeElement): AnnotatedInstance {
        val proto: ClassOrPackageDataWrapper.Class = getClassOrPackageDataWrapper(element) as ClassOrPackageDataWrapper.Class
        return AnnotatedInstance(element, proto)
    }

}