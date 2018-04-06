package arrow.optics

import arrow.common.utils.AbstractProcessor
import arrow.common.utils.isSealed
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
class OptikalProcessor : AbstractProcessor() {

  private val annotatedLenses = mutableListOf<AnnotatedOptic>()

  private val annotatedPrisms = mutableListOf<AnnotatedOptic>()

  private val annotatedIsos = mutableListOf<AnnotatedOptic>()

  private val annotatedOptional = mutableListOf<AnnotatedOptic>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
  override fun getSupportedAnnotationTypes() = setOf(
    lensesAnnotationClass.canonicalName,
    prismsAnnotationClass.canonicalName,
    isosAnnotationClass.canonicalName,
    optionalsAnnotationClass.canonicalName
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

    annotatedOptional += roundEnv
      .getElementsAnnotatedWith(optionalsAnnotationClass)
      .map(this::evalAnnotatedElement)

    if (roundEnv.processingOver()) {
      val generatedDir = File(this.generatedDir!!, "").also { it.mkdirs() }
      LensesFileGenerator(annotatedLenses, generatedDir).generate()
      PrismsFileGenerator(annotatedPrisms, generatedDir).generate()
      IsosFileGenerator(annotatedIsos, generatedDir).generate()
      OptionalFileGenerator(annotatedOptional, generatedDir).generate()
    }
  }

  private fun evalAnnotatedElement(element: Element): AnnotatedOptic = when {
    element.let { it.kotlinMetadata as? KotlinClassMetadata }?.data?.classProto?.isDataClass == true ->
      AnnotatedOptic(
        element as TypeElement,
        element.getClassData(),
        element.getConstructorTypesNames().zip(element.getConstructorParamNames(), ::Target)
      )

    else -> knownError(opticsAnnotationError(element, lensesAnnotationName, lensesAnnotationTarget))
  }

  private fun evalAnnotatedPrismElement(element: Element): AnnotatedOptic = when {
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

    else -> knownError(opticsAnnotationError(element, prismsAnnotationName, prismsAnnotationTarget))
  }

  private fun opticsAnnotationError(element: Element, annotationName: String, targetName: String): String = """
            |Cannot use $annotationName on ${element.enclosingElement}.${element.simpleName}.
            |It can only be used on $targetName.""".trimMargin()

  private fun evalAnnotatedIsoElement(element: Element): AnnotatedOptic = when {
    (element.kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isDataClass == true -> {
      val properties = element.getConstructorTypesNames().zip(element.getConstructorParamNames(), ::Target)

      if (properties.size > 10)
        knownError("${element.enclosingElement}.${element.simpleName} up to 10 constructor parameters is supported")
      else
        AnnotatedOptic(element as TypeElement, element.getClassData(), properties)
    }

    else -> knownError(opticsAnnotationError(element, isosAnnotationName, isosAnnotationTarget))
  }

}