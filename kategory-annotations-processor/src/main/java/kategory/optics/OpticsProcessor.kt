package kategory.optics

import com.google.auto.service.AutoService
import kategory.common.utils.AbstractProcessor
import kategory.common.utils.knownError
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.isDataClass
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeKind

@AutoService(Processor::class)
class OptikalProcessor : AbstractProcessor() {

    private val annotatedLenses = mutableListOf<AnnotatedLens.Element>()

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes() = setOf(lensesAnnotationClass.canonicalName)

    override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
        annotatedLenses += roundEnv
                .getElementsAnnotatedWith(lensesAnnotationClass)
                .map(this::evalAnnotatedElement)
                .map { annotatedLens ->
                    when (annotatedLens) {
                        is AnnotatedLens.InvalidElement -> knownError(annotatedLens.reason)
                        is AnnotatedLens.Element -> annotatedLens
                    }
                }

        if (roundEnv.processingOver()) {
            val generatedDir = File(this.generatedDir!!, "").also { it.mkdirs() }
            LensesFileGenerator(annotatedLenses, generatedDir).generate()
        }
    }

    fun evalAnnotatedElement(element: Element): AnnotatedLens = when {
        element.kotlinMetadata !is KotlinClassMetadata -> AnnotatedLens.InvalidElement("""
            |Cannot use @Lenses on ${element.enclosingElement}.${element.simpleName}.
            |It can only be used on data classes.""".trimMargin())

        (element.kotlinMetadata as KotlinClassMetadata).data.classProto.isDataClass ->
            AnnotatedLens.Element(
                    element as TypeElement,
                    element.enclosedElements
                            .filter { it.asType().kind == TypeKind.DECLARED }
                            .map { it as VariableElement })

        else -> AnnotatedLens.InvalidElement("${element.enclosingElement}.${element.simpleName} cannot be annotated with @Lenses")
    }

}