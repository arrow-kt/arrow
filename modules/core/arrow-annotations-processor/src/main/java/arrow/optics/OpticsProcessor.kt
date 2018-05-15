package arrow.optics

import arrow.common.messager.logE
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

  private val annotatedLenses = mutableListOf<AnnotatedOptic>()

  private val annotatedPrisms = mutableListOf<AnnotatedOptic>()

  private val annotatedIsos = mutableListOf<AnnotatedOptic>()

  private val annotatedOptional = mutableListOf<AnnotatedOptic>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes() = setOf(opticsAnnotationClass.canonicalName)

  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {

    roundEnv
      .getElementsAnnotatedWith(opticsAnnotationClass)
      .forEach { element ->
        if (element.classType == OTHER) {
          logE(element.otherClassTypeErrorMessage, element)
          return@forEach
        }

        if (element.hasNoCompanion) knownError("@optics annotated class $element needs to declare companion object.")

        element.normalizedTargets().forEach { target ->
          when (target) {
            LENS -> annotatedLenses.addIfNotNull(evalAnnotatedDataClass(element, element.lensErrorMessage))
            PRISM -> annotatedPrisms.addIfNotNull(evalAnnotatedPrismElement(element))
            ISO -> annotatedIsos.addIfNotNull(evalAnnotatedIsoElement(element))
            OPTIONAL -> annotatedOptional.addIfNotNull(evalAnnotatedDataClass(element, element.optionalErrorMessage))
          }
        }

      }

    if (roundEnv.processingOver()) {
      val generatedDir = File(this.generatedDir!!, "").also { it.mkdirs() }
      LensesFileGenerator(annotatedLenses, generatedDir).generate()
      PrismsFileGenerator(annotatedPrisms, generatedDir).generate()
      IsosFileGenerator(annotatedIsos, generatedDir).generate()
      OptionalFileGenerator(annotatedOptional, generatedDir).generate()
    }
  }

  private fun Element.normalizedTargets(): List<OpticsTarget> = with(getAnnotation(opticsAnnotationClass).targets) {
    when {
      isEmpty() -> if (classType == SEALED_CLASS) listOf(PRISM) else listOf(ISO, LENS, OPTIONAL)
      else -> toList()
    }

  }

  private fun evalAnnotatedDataClass(element: Element, errorMessage: String): AnnotatedOptic? = when (element.classType) {
    DATA_CLASS -> AnnotatedOptic(
      element as TypeElement,
      element.getClassData(),
      element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Target.Companion::invoke)
    )

    else -> null.also { logE(errorMessage, element) }
  }

  private fun evalAnnotatedPrismElement(element: Element): AnnotatedOptic? = when (element.classType) {
    SEALED_CLASS -> {
      val (nameResolver, classProto) = element.kotlinMetadata.let { it as KotlinClassMetadata }.data

      AnnotatedOptic(
        element as TypeElement,
        element.getClassData(),
        classProto.sealedSubclassFqNameList
          .map(nameResolver::getString)
          .map { it.replace('/', '.') }
          .map { Target(it, it.substringAfterLast(".")) }
      )
    }

    else -> null.also { logE(element.prismErrorMessage, element) }
  }

  private fun evalAnnotatedIsoElement(element: Element): AnnotatedOptic? = when (element.classType) {
    DATA_CLASS -> {
      val properties = element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Target.Companion::invoke)

      if (properties.size > 22) null.also { logE(element.isoTooBigErrorMessage, element) }
      else AnnotatedOptic(element as TypeElement, element.getClassData(), properties)
    }

    else -> null.also { logE(element.isoErrorMessage, element) }
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

  private fun <T> MutableCollection<T>.addIfNotNull(t: T?) {
    if (t != null) add(t)
  }
}
