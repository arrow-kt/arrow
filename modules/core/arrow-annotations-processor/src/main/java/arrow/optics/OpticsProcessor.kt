package arrow.optics

import arrow.common.utils.*
import arrow.optics.OpticsTarget.*
import arrow.optics.OpticsProcessor.Type.*
import com.google.auto.service.AutoService
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.isDataClass
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmMethodSignature
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.hasReceiver
import me.eugeniomarletti.kotlin.metadata.shadow.serialization.deserialization.getName
import me.eugeniomarletti.kotlin.metadata.shadow.utils.addToStdlib.ifNotEmpty
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class OpticsProcessor : AbstractProcessor() {

  private val annotatedTypes = mutableListOf<AnnotatedType>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes() = setOf(opticsAnnotationClass.canonicalName)

  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    roundEnv
      .getElementsAnnotatedWith(opticsAnnotationClass)
      .forEach { element ->
        when (element.type) {
          Type.SealedClass -> (element as TypeElement).toAnnotatedSumType()
          Type.DataClass -> (element as TypeElement).toAnnotatedProductType()
          Type.Function -> (element as ExecutableElement).toAnnotatedFunctionType()
          Type.Other -> knownError(element.otherClassTypeErrorMessage, element)
        }.let(annotatedTypes::add)
      }

    if (roundEnv.processingOver()) {
      annotatedTypes.forEach { ele ->
        ele.snippet()
          .groupBy(Snippet::fqName)
          .values
          .map {
            it.reduce { acc, snippet ->
              acc.copy(
                imports = acc.imports + snippet.imports,
                content = "${acc.content}\n${snippet.content}"
              )
            }
          }.forEach {
            val generatedDir = File("$generatedDir/${it.`package`.removeBackticks().replace(".", "/")}").also { it.mkdirs() }
            File(generatedDir, "${it.name.removeBackticks()}\$\$optics.kt").writeText(it.asFileText())
          }
      }
    }
}

private fun AnnotatedType.snippet(): List<Snippet> {
  fun AnnotatedType.normalizedTargets(): List<OpticsTarget> = with(element.getAnnotation(opticsAnnotationClass).targets) {
    when {
      isEmpty() -> when (element.type) {
        SealedClass -> listOf(PRISM, DSL)
        DataClass -> listOf(ISO, LENS, OPTIONAL, DSL)
        Function -> listOf(DSL)
        Other -> knownError(element.otherClassTypeErrorMessage, element)
      }
      else -> toList()
    }
  }

  return normalizedTargets().map { target ->
    when (target) {
      ISO -> isoSnippet
      LENS -> lensSnippet
      OPTIONAL -> optionalSnippet
      PRISM -> prismSnippet
      DSL -> dslSnippet
    }
  }
}

private fun TypeElement.toAnnotatedSumType() = AnnotatedSumType(this, getClassData(), kotlinMetadata.let { it as KotlinClassMetadata }.data.let { (nameResolver, classProto) ->
  classProto.sealedSubclassFqNameList
    .map(nameResolver::getString)
    .map { it.replace('/', '.') }
    .map { Focus(it, it.substringAfterLast(".").decapitalize()) }
}).also { if (hasNoCompanion) knownError("${opticsAnnotationClass.canonicalName} annotated class $this needs to declare companion object.", this) }

private fun TypeElement.toAnnotatedProductType() =
  AnnotatedProductType(this, getClassData(), getConstructorTypesNames().zip(getConstructorParamNames(), Focus.Companion::invoke))
    .also { if (hasNoCompanion) knownError("${opticsAnnotationClass.canonicalName} annotated class $this needs to declare companion object.", this) }

private fun ExecutableElement.toAnnotatedFunctionType(): AnnotatedFunctionType {
  val classElement = enclosingElement as TypeElement
  val proto = getClassOrPackageDataWrapper(classElement) as? ClassOrPackageDataWrapper.Package
    ?: knownError(dslErrorMessage, this)

  //TODO any better way to figure this out??
  val function = proto.functionList.firstOrNull {
    it.getJvmMethodSignature(proto.nameResolver)?.replace("\$Companion", "/Companion") == jvmMethodSignature
  } ?: knownError(dslErrorMessage, this)

  if (function.valueParameterList.isNotEmpty()) knownError(dslFunctionParametersMessage, this)

  fun ProtoBuf.Type.isNotOptic(proto: ClassOrPackageDataWrapper.Package): Boolean =
    (Optic.values.map(Optic::toString) + POptic.values.map(POptic::toString)).none { proto.nameResolver.getQualifiedClassName(className).replace("/", ".").removeBackticks().startsWith(it) }

  fun ProtoBuf.Type.isNotMonoOptic(): Boolean = when (argumentCount) {
    4 -> getArgument(0).type.extractFullName(proto) != getArgument(1).type.extractFullName(proto) && getArgument(2).type.extractFullName(proto) != getArgument(3).type.extractFullName(proto)
    2 -> true
    else -> false
  }

  fun ProtoBuf.Type.opticSourceAndFocus(proto: ClassOrPackageDataWrapper.Package): Pair<ProtoBuf.Type.Argument, ProtoBuf.Type.Argument> = when {
    isNotOptic(proto) -> knownError(dslWrongOptic, this@toAnnotatedFunctionType)
    isNotMonoOptic() -> knownError(dslWrongOptic, this@toAnnotatedFunctionType)
    argumentCount == 2 -> getArgument(0) to getArgument(1)
    argumentCount == 4 -> getArgument(1) to getArgument(3)
    else -> knownError(dslWrongOptic, this@toAnnotatedFunctionType)
  }

  val (source, focus) = function.returnType.opticSourceAndFocus(proto)

  return AnnotatedFunctionType(this, DslElement(
    `package` = proto.`package`,
    params = function.typeParameterList.map { proto.nameResolver.getString(it.name) }.let { it + it.nextGenericParam() },
    sourceType = function.typeParameterList.map { proto.nameResolver.getString(it.name) }.nextGenericParam(),
    dslName = function.name.let(proto.nameResolver::getName).asString(),
    originalFocus = source.type.extractFullName(proto),
    resultFocus = focus.type.extractFullName(proto),
    optic = if (function.hasReceiver()) "${function.receiverType.extractFullName(proto)}.${function.name.let(proto.nameResolver::getName)}()" else "${proto.`package`}.${function.name.let(proto.nameResolver::getName)}()",
    opticType = Optic.values.firstOrNull { proto.nameResolver.getQualifiedClassName(function.returnType.className).replace("/", ".").removeBackticks().startsWith(it.toString()) }
      ?: POptic.values.firstOrNull { proto.nameResolver.getQualifiedClassName(function.returnType.className).replace("/", ".").removeBackticks().startsWith(it.toString()) }?.monomorphic()
      ?: knownError(dslWrongOptic, this@toAnnotatedFunctionType)
  ))
}

private enum class Type {
  DataClass,
  SealedClass,
  Function,
  Other;
}

private val Element.type: Type
  get() = when {
    (kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isDataClass == true -> DataClass
    (kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isSealed == true -> SealedClass
    kind == ElementKind.METHOD -> Function
    else -> Other
  }

}
