package kategory.optics

import com.google.auto.service.AutoService
import kategory.common.utils.AbstractProcessor
import kategory.common.utils.asClassOrPackageDataWrapper
import kategory.common.utils.isSealed
import kategory.common.utils.knownError
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.extractFullName
import me.eugeniomarletti.kotlin.metadata.isDataClass
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.proto
import org.jetbrains.kotlin.serialization.ProtoBuf
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

import javax.lang.model.element.Element

@AutoService(Processor::class)
class OptikalProcessor : AbstractProcessor() {

    private val annotatedLenses = mutableListOf<AnnotatedElement>()

    private val annotatedPrisms = mutableListOf<AnnotatedElement>()

    private val annotatedIsos = mutableListOf<AnnotatedElement>()

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
    override fun getSupportedAnnotationTypes() = setOf(
            lensesAnnotationClass.canonicalName,
            prismsAnnotationClass.canonicalName,
            isosAnnotationClass.canonicalName
    )

    override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
        annotatedLenses += roundEnv
                .getElementsAnnotatedWith(lensesAnnotationClass)
                .map(this::evalAnnotatedElement)

        annotatedPrisms += roundEnv
                .getElementsAnnotatedWith(prismsAnnotationClass)
                .map(this::evalAnnotatedPrismElement)

        annotatedIsos += roundEnv
                .getElementsAnnotatedWith(isosAnnotationClass)
                .map(this::evalAnnotatedIsoElement)

        if (roundEnv.processingOver()) {
            val generatedDir = File(this.generatedDir!!, "").also { it.mkdirs() }
            LensesFileGenerator(annotatedLenses, generatedDir).generate()
            PrismsFileGenerator(annotatedPrisms, generatedDir).generate()
            IsosFileGenerator(annotatedIsos, generatedDir).generate()
        }
    }

    private fun evalAnnotatedElement(element: Element): AnnotatedElement = when {
        element.let { it.kotlinMetadata as? KotlinClassMetadata }?.data?.classProto?.isDataClass == true ->
            AnnotatedElement(
                    element as TypeElement,
                    getClassData(element),
                    getConstructorTypesNames(element).zip(getConstructorParamNames(element), ::Target)
            )

        else -> knownError(opticsAnnotationError(element, lensesAnnotationName, lensesAnnotationTarget))
    }

    private fun evalAnnotatedPrismElement(element: Element): AnnotatedElement = when {
        element.let { it.kotlinMetadata as? KotlinClassMetadata }?.data?.classProto?.isSealed == true -> {
            val (nameResolver, classProto) = element.kotlinMetadata.let { it as KotlinClassMetadata }.data

            AnnotatedElement(
                    element as TypeElement,
                    getClassData(element),
                    classProto.sealedSubclassFqNameList
                            .map(nameResolver::getString)
                            .map { it.replace('/', '.') }
                            .map { Target(it, it.substringAfterLast(".")) }
            )
        }

        else -> knownError(opticsAnnotationError(element, prismsAnnotationName, prismsAnnotationTarget))
    }

    private fun opticsAnnotationError(element: Element, annotationName: String, targetName: String): String = """
            |Cannot use $annotationName on ${element.enclosingElement}.${element.simpleName}.
            |It can only be used on $targetName.""".trimMargin()

    private fun evalAnnotatedIsoElement(element: Element): AnnotatedElement = when {
        (element.kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isDataClass == true -> {
            val properties = getConstructorTypesNames(element).zip(getConstructorParamNames(element), ::Target)

            if (properties.size < 2 || properties.size > 10)
                knownError("${element.enclosingElement}.${element.simpleName} constructor parameters should be between 2 and 10")
            else
                AnnotatedElement(element as TypeElement, getClassData(element), properties)
        }

        else -> knownError(opticsAnnotationError(element, isosAnnotationName, isosAnnotationTarget))
    }

    private fun getConstructorTypesNames(element: Element): List<String> = element.kotlinMetadata
            .let { it as KotlinClassMetadata }.data
            .let { data ->
                data.proto.constructorOrBuilderList
                        .first()
                        .valueParameterList
                        .map { it.type.extractFullName(data) }
            }

    private fun getConstructorParamNames(element: Element): List<String> = element.kotlinMetadata
            .let { it as KotlinClassMetadata }.data
            .let { (nameResolver, classProto) ->
                classProto.constructorOrBuilderList
                        .first()
                        .valueParameterList
                        .map(ProtoBuf.ValueParameter::getName)
                        .map(nameResolver::getString)
            }

    private fun getClassData(element: Element) = element.kotlinMetadata
            .let { it as KotlinClassMetadata }
            .data
            .asClassOrPackageDataWrapper(elementUtils.getPackageOf(element).toString())

}