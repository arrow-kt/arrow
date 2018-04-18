package arrow.optics

import arrow.common.messager.logE
import arrow.common.utils.AbstractProcessor
import arrow.common.utils.isSealed
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

        val type = element.getClassType()

        val targets = element.getAnnotation(opticsAnnotationClass).targets.toList()

        if (type == ClassType.OTHER) {
          logE("Only data and sealed classes can be annotated with optics annotation", element)
          return@forEach
        }

        val normalizedTargets = when {
          targets.isEmpty() ->
            when (type) {
              ClassType.SEALED_CLASS -> listOf(OpticsTarget.PRISM)
              else -> listOf(OpticsTarget.ISO, OpticsTarget.LENS, OpticsTarget.OPTIONAL, OpticsTarget.DSL)
            }
          targets.contains(OpticsTarget.DSL) ->
            when (type) {
              ClassType.DATA_CLASS -> targets + listOf(OpticsTarget.LENS, OpticsTarget.OPTIONAL)
              else -> {
                logE("Only data classes can have DSL target", element); emptyList()
              }

            }
          else -> targets
        }

        normalizedTargets.forEach { target ->

          when (target) {
            OpticsTarget.LENS -> annotatedLenses.addIfNotNull(evalAnnotatedLensElement(element))
            OpticsTarget.PRISM -> annotatedPrisms.addIfNotNull(evalAnnotatedPrismElement(element))
            OpticsTarget.ISO -> annotatedIsos.addIfNotNull(evalAnnotatedIsoElement(element))
            OpticsTarget.OPTIONAL -> annotatedOptional.addIfNotNull(evalAnnotatedOptionalElement(element))
            OpticsTarget.DSL -> annotatedBounded.addIfNotNull(evalAnnotatedDslElement(element))
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

  private fun evalAnnotatedOptionalElement(element: Element): AnnotatedOptic? = when (element.getClassType()) {
    ClassType.DATA_CLASS ->
      AnnotatedOptic(
        element as TypeElement,
        element.getClassData(),
        element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Target.Companion::invoke)
      )

    else -> {
      logE("Optionals can only be generated for data classes", element)
      null
    }

  }

  private fun evalAnnotatedLensElement(element: Element): AnnotatedOptic? = when (element.getClassType()) {
    ClassType.DATA_CLASS ->
      AnnotatedOptic(
        element as TypeElement,
        element.getClassData(),
        element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Target.Companion::invoke)
      )

    else -> {
      logE("Lenses can only be generated for data classes", element)
      null
    }
  }

  private fun evalAnnotatedDslElement(element: Element): AnnotatedOptic? = when (element.getClassType()) {
    ClassType.DATA_CLASS ->
      AnnotatedOptic(
        element as TypeElement,
        element.getClassData(),
        element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Target.Companion::invoke)
      )

    else -> {
      logE("DSL can only be generated for data classes", element)
      null
    }
  }

  private fun evalAnnotatedPrismElement(element: Element): AnnotatedOptic? = when (element.getClassType()) {
    ClassType.SEALED_CLASS -> {
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

    else -> {
      logE("Prisms can only be generated for sealed classes", element)
      null
    }
  }

  private fun evalAnnotatedIsoElement(element: Element): AnnotatedOptic? = when (element.getClassType()) {
    ClassType.DATA_CLASS -> {
      val properties = element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Target.Companion::invoke)

      if (properties.size > 22) {
        logE("""
          Iso generation is not supported for data classes with more than 22 constructor parameters
          """, element)
        null
      } else AnnotatedOptic(element as TypeElement, element.getClassData(), properties)
    }

    else -> {
      logE("Isos can only be generated for data classes", element)
      null
    }
  }

  private enum class ClassType {
    DATA_CLASS,
    SEALED_CLASS,
    OTHER;
  }

  private fun Element.getClassType(): ClassType = when {
    (kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isDataClass == true -> ClassType.DATA_CLASS
    (kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isSealed == true -> ClassType.SEALED_CLASS
    else -> ClassType.OTHER
  }
}
