package arrow.optics

import arrow.common.messager.logE
import arrow.common.utils.AbstractProcessor
import arrow.common.utils.isSealed
import arrow.optics.OpticsTarget.*
import arrow.optics.OpticsProcessor.ClassType.*
import com.google.auto.service.AutoService
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.isDataClass
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import org.jetbrains.kotlin.utils.addIfNotNull
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

  private val annotatedBounded = mutableListOf<AnnotatedOptic>()

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

        element.normalizedTargets().forEach { target ->
          when (target) {
            LENS -> annotatedLenses.addIfNotNull(evalAnnotatedDataClass(element, element.lensErrorMessage))
            PRISM -> annotatedPrisms.addIfNotNull(evalAnnotatedPrismElement(element))
            ISO -> annotatedIsos.addIfNotNull(evalAnnotatedIsoElement(element))
            OPTIONAL -> annotatedOptional.addIfNotNull(evalAnnotatedDataClass(element, element.optionalErrorMessage))
            DSL -> annotatedBounded.addIfNotNull(evalAnnotatedDslElement(element))
          }
        }

      }

    if (roundEnv.processingOver()) {
      val generatedDir = File(this.generatedDir!!, "").also { it.mkdirs() }
      LensesFileGenerator(annotatedLenses, generatedDir).generate()
      PrismsFileGenerator(annotatedPrisms, generatedDir).generate()
      IsosFileGenerator(annotatedIsos, generatedDir).generate()
      OptionalFileGenerator(annotatedOptional, generatedDir).generate()
      BoundSetterGenerator(annotatedBounded, generatedDir).generate()
    }
  }

  private fun Element.normalizedTargets(): List<OpticsTarget> = with(getAnnotation(opticsAnnotationClass).targets) {
    when {
      isEmpty() -> if (classType == SEALED_CLASS) listOf(PRISM, DSL) else listOf(ISO, LENS, OPTIONAL, DSL)
      contains(DSL) -> toList() + if (classType == SEALED_CLASS) listOf(PRISM) else listOf(LENS, OPTIONAL, PRISM)
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

  private fun evalAnnotatedDslElement(element: Element): AnnotatedOptic? = when (element.classType) {
    DATA_CLASS -> AnnotatedOptic(
      element as TypeElement,
      element.getClassData(),
      element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Target.Companion::invoke)
    )
    SEALED_CLASS -> evalAnnotatedPrismElement(element)
    OTHER -> null.also { logE(element.dslErrorMessage, element) }
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

  private val Element.otherClassTypeErrorMessage
    get() = """
      |$this cannot be annotated with @optics
      | ^
      |
      |Only data and sealed classes can be annotated with @optics annotation""".trimMargin()

  private val Element.lensErrorMessage
    get() = """
      |Cannot generate arrow.optics.Lens for $this
      |                                       ^
      |arrow.optics.OpticsTarget.LENS is an invalid @optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

  private val Element.optionalErrorMessage
    get() = """
      |Cannot generate arrow.optics.Optional for $this
      |                                           ^
      |arrow.optics.OpticsTarget.OPTIONAL is an invalid @optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

  private val Element.prismErrorMessage
    get() = """
      |Cannot generate arrow.optics.Prism for $this
      |                                        ^
      |arrow.optics.OpticsTarget.PRISM is an invalid @optics argument for $this.
      |It is only valid for sealed classes.
      """.trimMargin()

  private val Element.isoErrorMessage
    get() = """
      |Cannot generate arrow.optics.Iso for $this
      |                                      ^
      |arrow.optics.OpticsTarget.ISO is an invalid @optics argument for $this.
      |It is only valid for data classes.
      """.trimMargin()

  private val Element.isoTooBigErrorMessage
    get() = """
      |Cannot generate arrow.optics.Iso for $this
      |                                      ^
      |Iso generation is supported for data classes with up to 22 constructor parameters.
      """.trimMargin()

  private val Element.dslErrorMessage
    get() = """
      |Cannot generate DSL (arrow.optics.BoundSetter) for $this
      |                                           ^
      |arrow.optics.OpticsTarget.DSL is an invalid @optics argument for $this.
      |It is only valid for data classes and sealed classes.
      """.trimMargin()

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
