package arrow.optics

import arrow.common.messager.logE
import arrow.common.utils.AbstractProcessor
import arrow.common.utils.isSealed
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
import javax.tools.Diagnostic

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

              val targets = element.getAnnotation(opticsAnnotationClass).targets

              if (type == ClassType.OTHER) {
                logE("Only data and sealed classes can be annotated with optics annotation", element)
                return@forEach
              }

              val normalizedTargets = if (targets.isEmpty()) {

                if (type == ClassType.SEALED_CLASS) {
                  listOf(OpticsTarget.PRISM)
                } else {
                  listOf(OpticsTarget.ISO, OpticsTarget.LENS, OpticsTarget.OPTIONAL, OpticsTarget.DSL)
                }

              } else if (targets.contains(OpticsTarget.DSL)) {

                if (type != ClassType.DATA_CLASS) {
                  logE("Only data classes can have DSL target", element)
                  emptyList()
                } else {
                  targets.toSet().plus(listOf(OpticsTarget.LENS, OpticsTarget.OPTIONAL)).toList()
                }

              } else {
                targets.toList()
              }

              normalizedTargets.forEach { target ->

                when (target) {
                  OpticsTarget.LENS -> annotatedLenses.addAll(evalAnnotatedLensElement(element).singleToList())
                  OpticsTarget.PRISM -> annotatedPrisms.addAll(evalAnnotatedPrismElement(element).singleToList())
                  OpticsTarget.ISO -> annotatedIsos.addAll(evalAnnotatedIsoElement(element).singleToList())
                  OpticsTarget.OPTIONAL -> annotatedOptional.addAll(evalAnnotatedOptionalElement(element).singleToList())
                  OpticsTarget.DSL -> annotatedBounded.addAll(evalAnnotatedDslElement(element).singleToList())
                }

              }

            }

    if (roundEnv.processingOver()) {
      val generatedDir = File(this.generatedDir!!, "").also { it.mkdirs() }
      LensesFileGenerator(annotatedLenses, generatedDir).generate()
      PrismsFileGenerator(annotatedPrisms, generatedDir).generate()
      IsosFileGenerator(annotatedIsos, generatedDir).generate()
      OptionalFileGenerator(annotatedOptional, generatedDir).generate()

      LensesFileGenerator(annotatedBounded, generatedDir).generate()
      OptionalFileGenerator(annotatedBounded, generatedDir).generate()
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

  private fun evalAnnotatedLensElement(element: Element): AnnotatedOptic? = when {
    element.let { it.kotlinMetadata as? KotlinClassMetadata }?.data?.classProto?.isDataClass == true ->
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

  private fun evalAnnotatedDslElement(element: Element): AnnotatedOptic? = when {
    element.let { it.kotlinMetadata as? KotlinClassMetadata }?.data?.classProto?.isDataClass == true ->
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

  private fun evalAnnotatedPrismElement(element: Element): AnnotatedOptic? = when {
    element.let { it.kotlinMetadata as? KotlinClassMetadata }?.data?.classProto?.isSealed == true -> {
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
      knownError(element, OpticsTarget.PRISM)
      null
    }
  }

  private fun evalAnnotatedIsoElement(element: Element): AnnotatedOptic? = when {
    (element.kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isDataClass == true -> {
      val properties = element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Target.Companion::invoke)

      if (properties.size > 10) {
        logE("""
          |Cannot generate arrow.optics.Iso for ${element.enclosingElement}.${element.simpleName}.
          |Iso generation is supported up to 10 constructor parameters is supported
          """, element)
        null
      } else AnnotatedOptic(element as TypeElement, element.getClassData(), properties)
    }

    else -> {
      knownError(element, OpticsTarget.ISO)
      null
    }
  }

  private fun knownError(element: Element, target: OpticsTarget) {
    messager.printMessage(Diagnostic.Kind.ERROR, "Annotation $target is invalid for element", element)
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

  private fun <T> T?.singleToList(): List<T> = if (this == null) {
      emptyList()
    } else {
      listOf(this)
    }

}
