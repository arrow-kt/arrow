package kategory.optics

import com.google.auto.service.AutoService
import kategory.common.utils.AbstractProcessor
import kategory.common.utils.knownError
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.isDataClass
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.modality
import org.jetbrains.kotlin.serialization.ProtoBuf
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class OptikalProcessor : AbstractProcessor() {

    private val annotatedLenses = mutableListOf<AnnotatedLens.Element>()

    private val annotatedPrisms = mutableListOf<AnnotatedPrism.Element>()

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

    override fun getSupportedAnnotationTypes() = setOf(
            lensesAnnotationClass.canonicalName,
            prismsAnnotationClass.canonicalName
    )

    override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
        annotatedLenses += roundEnv
                .getElementsAnnotatedWith(lensesAnnotationClass)
                .map(this::evalAnnotatedElement)

        annotatedPrisms += roundEnv
                .getElementsAnnotatedWith(prismsAnnotationClass)
                .map(this::evalAnnotatedPrismElement)

        if (roundEnv.processingOver()) {
            val generatedDir = File(this.generatedDir!!, "").also { it.mkdirs() }
            LensesFileGenerator(annotatedLenses, generatedDir).generate()
            PrismsFileGenerator(annotatedPrisms, generatedDir).generate()
        }
    }

    private fun evalAnnotatedElement(element: Element): AnnotatedLens.Element = when {
        element.let { it.kotlinMetadata as? KotlinClassMetadata }?.data?.classProto?.isDataClass == true ->
            AnnotatedLens.Element(
                    element as TypeElement,
                    element.enclosedElements.firstOrNull { it.kind == ElementKind.CONSTRUCTOR }
                            ?.let { it as ExecutableElement }
                            ?.parameters ?: emptyList()
            )

        else -> knownError(opticsAnnotationError(element, lensesAnnotationName, lensesAnnotationTarget))
    }

    private fun evalAnnotatedPrismElement(element: Element): AnnotatedPrism.Element = when {
        element.let { it.kotlinMetadata as? KotlinClassMetadata }?.data?.classProto?.isSealed == true -> {
            val (nameResolver, classProto) = element.kotlinMetadata.let { it as KotlinClassMetadata }.data

            AnnotatedPrism.Element(
                    element as TypeElement,
                    classProto.sealedSubclassFqNameList
                            .map(nameResolver::getString)
                            .map { it.replace('/', '.') }
                            .mapNotNull(elementUtils::getTypeElement)
            )
        }

        else -> knownError(opticsAnnotationError(element, prismsAnnotationName, prismsAnnotationTarget))
    }

    private fun opticsAnnotationError(element: Element, annotationName: String, targetName: String): String = """
            |Cannot use $annotationName on ${element.enclosingElement}.${element.simpleName}.
            |It can only be used on $targetName.""".trimMargin()

    private val ProtoBuf.Class.isSealed
        get() = modality == ProtoBuf.Modality.SEALED

}