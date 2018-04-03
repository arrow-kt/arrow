package arrow.optics

import arrow.common.messager.logW
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

@AutoService(Processor::class)
class OptikalProcessor : AbstractProcessor() {

  private val annotatedLenses = mutableListOf<AnnotatedOptic>()

  private val annotatedPrisms = mutableListOf<AnnotatedOptic>()

  private val annotatedIsos = mutableListOf<AnnotatedOptic>()

  private val annotatedOptional = mutableListOf<AnnotatedOptic>()

  private val annotatedBounded = mutableListOf<AnnotatedOptic>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes() = setOf(opticsAnnotationClass.canonicalName)

  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    annotatedLenses += roundEnv
      .getElementsAnnotatedWith(opticsAnnotationClass)
      .filter { it.getAnnotation(opticsAnnotationClass).targets.contains(OpticsTarget.LENS) }
      .mapNotNull(this::evalAnnotatedElement)

    annotatedPrisms += roundEnv
      .getElementsAnnotatedWith(opticsAnnotationClass)
      .filter { it.getAnnotation(opticsAnnotationClass).targets.contains(OpticsTarget.PRISM) }
      .mapNotNull(this::evalAnnotatedPrismElement)

    annotatedIsos += roundEnv
      .getElementsAnnotatedWith(opticsAnnotationClass)
      .filter { it.getAnnotation(opticsAnnotationClass).targets.contains(OpticsTarget.ISO) }
      .mapNotNull(this::evalAnnotatedIsoElement)

    annotatedOptional += roundEnv
      .getElementsAnnotatedWith(opticsAnnotationClass)
      .filter { it.getAnnotation(opticsAnnotationClass).targets.contains(OpticsTarget.OPTIONAL) }
      .mapNotNull(this::evalAnnotatedElement)

    annotatedBounded += roundEnv
      .getElementsAnnotatedWith(opticsAnnotationClass)
      .filter { it.getAnnotation(opticsAnnotationClass).targets.contains(OpticsTarget.DSL) }
      .mapNotNull(this::evalAnnotatedElement)

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

  private fun evalAnnotatedElement(element: Element): AnnotatedOptic? = when {
    element.let { it.kotlinMetadata as? KotlinClassMetadata }?.data?.classProto?.isDataClass == true ->
      AnnotatedOptic(
        element as TypeElement,
        element.getClassData(),
        element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Target.Companion::invoke)
      )

    else -> null
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

    else -> null
  }

  private fun evalAnnotatedIsoElement(element: Element): AnnotatedOptic? = when {
    (element.kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isDataClass == true -> {
      val properties = element.getConstructorTypesNames().zip(element.getConstructorParamNames(), Target.Companion::invoke)

      if (properties.size > 10) {
        logW("""
          |Cannot generate arrow.optics.Iso for ${element.enclosingElement}.${element.simpleName}.
          |Iso generation is supported up to 10 constructor parameters is supported
          """)
        null
      } else AnnotatedOptic(element as TypeElement, element.getClassData(), properties)
    }

    else -> null
  }

}