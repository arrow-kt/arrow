package arrow.higherkinds

import com.google.auto.service.AutoService
import arrow.common.utils.AbstractProcessor
import arrow.common.utils.knownError
import java.io.File
import java.util.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class HigherKindsProcessor : AbstractProcessor() {

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes(): Set<String> = setOf(higherKindsAnnotationClass.canonicalName)

  /**
   * Processor entry point
   */
  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {

    val generator = HigherKindsFileGenerator(filer)

    roundEnv
      .getElementsAnnotatedWith(higherKindsAnnotationClass)
      .map { element ->
        when (element.kind) {
          ElementKind.CLASS -> processClass(element as TypeElement)
          ElementKind.INTERFACE -> processClass(element as TypeElement)
          else -> knownError("$higherKindsAnnotationName can only be used on classes")
        }
      }
      .forEach {
        generator.generate(it)
      }
  }

  private fun processClass(element: TypeElement): AnnotatedHigherKind {
    val proto = getClassOrPackageDataWrapper(element)
    return AnnotatedHigherKind(element, proto)
  }
}
