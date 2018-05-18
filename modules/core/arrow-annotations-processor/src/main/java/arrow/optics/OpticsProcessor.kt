package arrow.optics

import arrow.common.utils.AbstractProcessor
import arrow.common.utils.isSealed
import arrow.optics.OpticsTarget.*
import arrow.optics.OpticsProcessor.ClassType.*
import arrow.common.utils.knownError
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

  private val annotatedElement = mutableListOf<Pair<AnnotatedOptic, List<Target>>>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes() = setOf(opticsAnnotationClass.canonicalName)

  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    roundEnv
      .getElementsAnnotatedWith(opticsAnnotationClass)
      .forEach { element ->
        if (element.classType == OTHER) knownError(element.otherClassTypeErrorMessage, element)
        if (element.hasNoCompanion) knownError("@optics annotated class $element needs to declare companion object.")

        val targets = element.normalizedTargets().map { target ->
          when (target) {
            LENS -> evalAnnotatedDataClass(element, element.lensErrorMessage).let(::LensOptic)
            OPTIONAL -> evalAnnotatedDataClass(element, element.lensErrorMessage).let(::OptionalOptic)
            ISO -> evalAnnotatedIsoElement(element).let(::IsoOptic)
            PRISM -> evalAnnotatedPrismElement(element).let(::PrismOptic)
          }
        }

        annotatedElement.add(AnnotatedOptic(element as TypeElement, element.getClassData()) to targets)
      }

    if (roundEnv.processingOver()) {
      val generatedDir = File(this.generatedDir!!, "").also { it.mkdirs() }
      annotatedElement.map { (element, targets) ->
        element to targets.map {
          when (it) {
            is IsoOptic -> generateIsos(element, it)
            is PrismOptic -> generatePrisms(element, it)
            is LensOptic -> generateLenses(element, it)
            is OptionalOptic -> generateOptionals(element, it)
          }
        }.reduce(Snippet::plus).let { (import, content) ->
          """
            |package ${element.packageName}
            |
            |${import.joinToString(separator = "\n")}
            |
            |$content
            |""".trimMargin()
        }
      }.forEach { (element, snippet) ->
        File(generatedDir, "optics.arrow.${element.sourceName}").writeText(snippet)
      }
    }
  }

  private fun Element.normalizedTargets(): List<OpticsTarget> = with(getAnnotation(opticsAnnotationClass).targets) {
    when {
      isEmpty() -> if (classType == SEALED_CLASS) listOf(PRISM) else listOf(ISO, LENS, OPTIONAL)
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
        .map { Focus(it, it.substringAfterLast(".")) }
    }

    else -> knownError(element.prismErrorMessage, element)
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
