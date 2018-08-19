package arrow.meta.processor

import arrow.common.messager.logW
import arrow.common.utils.*
import arrow.meta.ast.*
import arrow.meta.encoder.MetaEncoder
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf as proto

abstract class MetaProcessor<A : Annotation>(
  private val annotations: List<KClass<A>>,
  private val encoder: MetaEncoder<Tree>
) : AbstractProcessor() {

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes(): Set<String> = annotations.map { it.java.canonicalName }.toSet()

  sealed class AnnotatedElement {
    data class Interface(val fileSpec: FileSpec.Builder, val type: Type) : AnnotatedElement()
    data class Class(val fileSpec: FileSpec.Builder, val type: Type) : AnnotatedElement()
  }

  abstract fun transform(annotatedElement: AnnotatedElement): FileSpec.Builder

  private val transformList = mutableListOf<FileSpec>()

  /**
   * Processor entry point
   */
  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    this.annotations.forEach { annotation ->
      transformList += roundEnv
        .getElementsAnnotatedWith(annotation.java)
        .flatMap { element ->
          //          try {
          val result = when (element.kind) {
            ElementKind.INTERFACE -> {
              val typeElement = element as TypeElement
              val className = typeElement.asClassName()
              transform(
                AnnotatedElement.Interface(
                  fileSpec = FileSpec.builder(className.packageName, className.simpleName),
                  type = typeElement.asMetaType()
                )
              )
            }
            ElementKind.CLASS -> {
              val typeElement = element as TypeElement
              val type = encoder.encode(typeElement)
              val className = typeElement.asClassName()
              transform(
                AnnotatedElement.Class(
                  fileSpec = FileSpec.builder(className.packageName, className.simpleName),
                  typeElement = typeElement
                )
              )
            }
            else -> knownError("Unsupported meta annotation: $annotation over ${element.kind.name} ")
          }
          listOf(result.build())
        }
      if (roundEnv.processingOver()) {
        val generatedDir = File(this.generatedDir!!, annotation.java.simpleName).also { it.mkdirs() }
        transformList.forEach { it.writeTo(generatedDir) }
      }
    }
  }

  data class TypeClassInstance(
    val instance: TypeElement,
    val typeClass: TypeElement,
    val dataType: TypeElement)

  fun TypeElement.typeClassInstanceInfo(): TypeClassInstance? {
    return if (interfaces.isEmpty()) null
    else {
      val typeClassName = interfaces[0].asTypeName()
      if (typeClassName is ParameterizedTypeName && typeClassName.typeArguments.isNotEmpty()) {
        val typeClass = elementUtils.getTypeElement(typeClassName.rawType.toString())
        val dataTypeName = typeClassName.typeArguments[0]
        val dataType = if (dataTypeName is ParameterizedTypeName && dataTypeName.isKinded()) {
          val normalizedType = dataTypeName.normalizeKind()
          if (normalizedType is ParameterizedTypeName) {
            logW("parametric dataType: ${normalizedType.rawType} ($dataTypeName)")
            elementUtils.getTypeElement(normalizedType.rawType.toString())
          } else {
            logW("simple dataType: ${normalizedType} ($dataTypeName)")
            elementUtils.getTypeElement(normalizedType.toString())
          }
        } else elementUtils.getTypeElement(dataTypeName.toString().findNestedType())
        logW("TypeClassInstance(${this.asClassName()}, ${typeClass.asClassName()}, ${dataType})")
        TypeClassInstance(this, typeClass, dataType)
      } else null
    }
  }

  private fun String.findNestedType(): String =
    removeBackticks()
      .replace("arrow.Kind<(.*?)>".toRegex(), "$1")
      .replace("in ", "")
      .replace("out ", "")
      .substringBefore("<")
      .replace(".For", ".")
      .removeSuffix("PartialOf")
      .removeSuffix("Of")
      .substringBefore(",")

  fun TypeName.isKinded(): Boolean =
    toString()
      .replace("in ", "")
      .replace("out ", "")
      .startsWith("arrow.Kind")


  fun ParameterizedTypeName.normalizeKind(): TypeName =
    if (!isKinded()) this //not a higher kind in the shape of Kind<F, A>
    else {
      val dataType: TypeName = typeArguments[0]
      if (dataType is ParameterizedTypeName && dataType.isKinded()) {
        val dataTypeSimpleName = dataType.rawType.simpleName
        val result = dataTypeSimpleName.findNestedType()
        val className = ClassName(dataType.rawType.packageName, result)
        className.parameterizedBy(*typeArguments.drop(1).toTypedArray())
      } else ClassName.bestGuess(dataType.toString().findNestedType())
    }

  fun TypeName.asTypeSpec(): TypeSpec? =
    (this as? ParameterizedTypeName)?.rawType?.canonicalName.let {
      elementUtils.getTypeElement(it).asTypeSpec()
    }

  fun ClassOrPackageDataWrapper.Class.sealedSubClassNames(): List<TypeName> =
    if (classProto.sealedSubclassFqNameList.isNotEmpty())
      classProto.sealedSubclassFqNameList.map { ClassName(`package`, nameResolver.getString(it)) }
    else emptyList()

  fun ClassOrPackageDataWrapper.Class.nestedClassNames(): List<TypeName> =
    if (classProto.nestedClassNameList.isNotEmpty())
      classProto.nestedClassNameList.map { ClassName(`package`, nameResolver.getString(it)) }
    else emptyList()

  fun ClassOrPackageDataWrapper.Class.superTypes(): List<TypeName> =
    if (classProto.supertypeList.isNotEmpty()) {
      classProto.supertypeList.map { ClassName(`package`, it.extractFullName(this)) }
    } else emptyList()

  sealed class KindApplicationStrategy {
    object MostConcrete : KindApplicationStrategy()
    object SimplifyAlias : KindApplicationStrategy()
    object Noop : KindApplicationStrategy()
  }

  //kotlin.jvm.functions.Function1<A,arrow.Kind<arrow.core.ForTry,B>>

  private fun TypeName.applyKind(strategy: KindApplicationStrategy): TypeName {
    tailrec fun loop(source: String): String {
      val capitalized = if (source.contains(".")) source else source.capitalize()
      val dejavu = capitalized
        .replace("? extends ", "")
        .replace("? super ", "")
      val result = when (strategy) {
        KindApplicationStrategy.MostConcrete -> dejavu.replace("arrow.Kind<(.*?)For(.*?),(.*?)>".toRegex(), "$1$2<$3>")
        KindApplicationStrategy.SimplifyAlias -> dejavu.replace("arrow.Kind<(.*?)For(.*?),(.*?)>".toRegex(), "$1$2Of<$3>")
        KindApplicationStrategy.Noop -> dejavu
      }
      return when {
        strategy == KindApplicationStrategy.Noop -> result
        result.count { it == '<' } > 1 -> loop(result.substringAfter("<").substringBeforeLast(">"))
        else -> result
      }
    }

    val result = loop(toString())
    val rawType = result.substringBefore("<")
    val pckg = rawType.substringBeforeLast(".")
    val simpleName = rawType.substringBeforeLast(".")
    val typeArgs = result.substringAfter("<").substringBeforeLast(">").split(", ")
    return if (typeArgs.isNotEmpty()) ClassName(pckg, simpleName).parameterizedBy(*typeArgs.map { TypeVariableName(it) }.toTypedArray())
    else ClassName(pckg, simpleName)
  }

}






