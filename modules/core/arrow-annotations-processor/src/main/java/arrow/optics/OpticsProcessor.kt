package arrow.optics

import arrow.common.utils.AbstractProcessor
import arrow.common.utils.isSealed
import arrow.optics.OpticsTarget.*
import arrow.optics.OpticsProcessor.ClassType.*
import arrow.common.utils.knownError
import arrow.common.utils.removeBackticks
import com.google.auto.service.AutoService
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.isDataClass
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class OpticsProcessor : AbstractProcessor() {

  private val annotatedEles = mutableListOf<AnnotatedElement>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes() = setOf(opticsAnnotationClass.canonicalName)

  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    roundEnv
      .getElementsAnnotatedWith(opticsAnnotationClass)
      .forEach { element ->
        if (element.classType == OTHER) knownError(element.otherClassTypeErrorMessage, element)
        if (element.hasNoCompanion) knownError("@optics annotated class $element needs to declare companion object.")

        val targets: List<Target> = element.normalizedTargets().map { target ->
          when (target) {
            LENS -> evalAnnotatedDataClass(element, element.lensErrorMessage).let(::LensTarget)
            OPTIONAL -> evalAnnotatedDataClass(element, element.optionalErrorMessage).let(::OptionalTarget)
            ISO -> evalAnnotatedIsoElement(element).let(::IsoTarget)
            PRISM -> evalAnnotatedPrismElement(element).let(::PrismTarget)
            DSL -> evalAnnotatedDslElement(element)
          }
        }

        annotatedEles.add(AnnotatedElement(element as TypeElement, element.getClassData(), targets))
      }

    if (roundEnv.processingOver()) {
      annotatedEles
        .forEach { ele ->
          ele.snippets()
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

  private fun AnnotatedElement.snippets(): List<Snippet> = this.targets.map {
    when (it) {
      is IsoTarget -> generateIsos(this, it)
      is PrismTarget -> generatePrisms(this, it)
      is LensTarget -> generateLenses(this, it)
      is OptionalTarget -> generateOptionals(this, it)
      is SealedClassDsl -> generatePrismDsl(this, it)
      is DataClassDsl -> generateOptionalDsl(this, it) + generateLensDsl(this, it)
    }
  }

  private fun Element.normalizedTargets(): List<OpticsTarget> = with(getAnnotation(opticsAnnotationClass).targets) {
    when {
      isEmpty() -> if (classType == SEALED_CLASS) listOf(PRISM, DSL) else listOf(ISO, LENS, OPTIONAL, DSL)
      else -> toList()
    }
  }

  private fun evalAnnotatedDataClass(element: Element, errorMessage: String): List<Focus> = when (element.classType) {
    DATA_CLASS -> element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Focus.Companion::invoke)
    else -> knownError(errorMessage, element)
  }

  private fun evalAnnotatedPrismElement(element: Element): List<Focus> = when (element.classType) {
    SEALED_CLASS -> element.kotlinMetadata.let { it as KotlinClassMetadata }.data.let { (nameResolver, classProto) ->
      classProto.sealedSubclassFqNameList
        .map(nameResolver::getString)
        .map { it.replace('/', '.') }
        .map { Focus(it, it.substringAfterLast(".").decapitalize()) }
    }

    else -> knownError(element.prismErrorMessage, element)
  }

  private fun evalAnnotatedDslElement(element: Element): Target = when (element.classType) {
    DATA_CLASS -> DataClassDsl(element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Focus.Companion::invoke))
    SEALED_CLASS -> SealedClassDsl(evalAnnotatedPrismElement(element))
    OTHER -> knownError(element.dslErrorMessage, element)
  }

  private fun evalAnnotatedIsoElement(element: Element): List<Focus> = when (element.classType) {
    DATA_CLASS -> element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Focus.Companion::invoke)
      .takeIf { it.size <= 22 } ?: knownError(element.isoTooBigErrorMessage, element)
    else -> knownError(element.isoErrorMessage, element)
  }

  private enum class ClassType {
    DATA_CLASS,
    SEALED_CLASS,
    OTHER;
  }

  private val Element.classType: ClassType
    get() = when {
      (kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isDataClass == true -> DATA_CLASS
      (kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isSealed == true -> SEALED_CLASS
      else -> OTHER
    }

}
