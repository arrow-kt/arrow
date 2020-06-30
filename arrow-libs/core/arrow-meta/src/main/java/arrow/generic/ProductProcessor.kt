package arrow.generic

import arrow.DerivingTarget
import arrow.DerivingTarget.APPLICATIVE
import arrow.DerivingTarget.EQ
import arrow.DerivingTarget.HLIST
import arrow.DerivingTarget.MONOID
import arrow.DerivingTarget.SEMIGROUP
import arrow.DerivingTarget.SHOW
import arrow.DerivingTarget.TUPLED
import arrow.common.messager.log
import arrow.common.utils.AbstractProcessor
import arrow.common.utils.knownError
import com.google.auto.service.AutoService
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.isDataClass
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class ProductProcessor : AbstractProcessor() {

  private val annotatedProduct = mutableListOf<AnnotatedGeneric>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes() = setOf(productAnnotationClass.canonicalName)

  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    annotatedProduct += roundEnv
      .getElementsAnnotatedWith(productAnnotationClass)
      .map(this::evalAnnotatedProductElement)

    if (roundEnv.processingOver()) {
      ProductFileGenerator(annotatedProduct, filer).generate { log(it) }
    }
  }

  private fun productAnnotationError(element: Element, annotationName: String, targetName: String): String = """
            |Cannot use $annotationName on ${element.enclosingElement}.${element.simpleName}.
            |It can only be used on $targetName.""".trimMargin()

  private fun productCompanionError(element: Element, annotationName: String): String =
    "$annotationName annotated class $element needs to declare companion object."

  private fun evalAnnotatedProductElement(element: Element): AnnotatedGeneric = when {
    element.hasNoCompanion -> knownError(productCompanionError(element, productAnnotationName))
    (element.kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isDataClass == true -> {
      val elementClassData = element.getClassData()
      val paramNames = element.getConstructorParamNames()
      val typeNames = element.getConstructorTypesNames()
      val properties = paramNames.zip(typeNames).map { Target(it.second, it.first) }
      if (properties.size > 22)
        knownError("${element.enclosingElement}.${element.simpleName} up to 22 constructor parameters is supported")
      else
        AnnotatedGeneric(element as TypeElement, elementClassData, properties, element.normalizedDerivingTargets())
    }

    else -> knownError(productAnnotationError(element, productAnnotationName, productAnnotationTarget))
  }

  private fun derivingAdditions(target: DerivingTarget): List<DerivingTarget> = when (target) {
    MONOID -> listOf(SEMIGROUP)
    APPLICATIVE -> listOf(TUPLED)
    else -> emptyList()
  }

  private fun Element.normalizedDerivingTargets(): List<DerivingTarget> = with(getAnnotation(productAnnotationClass).deriving) {
    when {
      isEmpty() -> listOf(SEMIGROUP, MONOID, TUPLED, HLIST, APPLICATIVE, EQ, SHOW)
      else -> toList().flatMap { derivingAdditions(it) + it }
    }
  }
}
