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

    private val annotatedLenses = mutableListOf<AnnotatedLens.Element>()

    private val annotatedPrisms = mutableListOf<AnnotatedPrism.Element>()

    private val annotatedIsos = mutableListOf<AnnotatedIso.Element>()

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

    private fun evalAnnotatedElement(element: Element): AnnotatedLens.Element = when {
        element.let { it.kotlinMetadata as? KotlinClassMetadata }?.data?.classProto?.isDataClass == true ->
            AnnotatedLens.Element(
                    element as TypeElement,
                    getConstructorTypesNames(element).zip(getConstructorParamNames(element), ::Variable)
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

    private fun evalAnnotatedIsoElement(element: Element): AnnotatedIso.Element = when {
        (element.kotlinMetadata as KotlinClassMetadata).data.classProto.isDataClass -> {
            val properties = getConstructorTypesNames(element).zip(getConstructorParamNames(element), ::Variable)

            if (properties.size < 2 || properties.size > 10)
                knownError("${element.enclosingElement}.${element.simpleName} constructor parameters should be between 2 and 10")
            else
                AnnotatedIso.Element(element as TypeElement, element.kotlinMetadata.let { it as KotlinClassMetadata }
                        .data.asClassOrPackageDataWrapper(elementUtils.getPackageOf(element).toString()) , properties)
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

}