package arrow.generic

import arrow.common.messager.logW
import com.google.auto.service.AutoService
import arrow.common.utils.AbstractProcessor
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.asClassOrPackageDataWrapper
import arrow.common.utils.knownError
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
class ProductProcessor : AbstractProcessor() {
    
    private val annotatedProduct = mutableListOf<AnnotatedGeneric>()

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
    
    override fun getSupportedAnnotationTypes() = setOf(productAnnotationClass.canonicalName)

    override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
        annotatedProduct += roundEnv
                .getElementsAnnotatedWith(productAnnotationClass)
                .map(this::evalAnnotatedProductElement)

        if (roundEnv.processingOver()) {
            val generatedDir = File(this.generatedDir!!, "").also { it.mkdirs() }
            ProductFileGenerator(annotatedProduct, generatedDir).generate()
        }
    }

    private fun productAnnotationError(element: Element, annotationName: String, targetName: String): String = """
            |Cannot use $annotationName on ${element.enclosingElement}.${element.simpleName}.
            |It can only be used on $targetName.""".trimMargin()

    private fun evalAnnotatedProductElement(element: Element): AnnotatedGeneric = when {
        (element.kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isDataClass == true -> {
            val elementClassData = getClassData(element)
            val paramNames = getConstructorParamNames(element)
            val typeNames = getConstructorTypesNames(element)
            val properties = paramNames.zip(typeNames).map { Target(it.second,it.first) }
            if (properties.size > 22)
                knownError("${element.enclosingElement}.${element.simpleName} up to 22 constructor parameters is supported")
            else
                AnnotatedGeneric(element as TypeElement, elementClassData, properties)
        }

        else -> knownError(productAnnotationError(element, productAnnotationName, productAnnotationTarget))
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