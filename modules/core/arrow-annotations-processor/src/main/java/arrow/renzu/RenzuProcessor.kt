package arrow.renzu

import arrow.common.utils.AbstractProcessor
import arrow.instances.*
import com.google.auto.service.AutoService
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class RenzuProcessor(val isolateForTests: Boolean = false) : AbstractProcessor() {

  val annotatedList = mutableListOf<AnnotatedInstance>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes(): Set<String> = setOf(instanceAnnotationClass.canonicalName)

  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    //TODO Rewire based on arrow-meta after 0.8.0 (impl below)
  }

  /**
   * Processor entry point
   */
//  fun onProcessTODO(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
//    annotatedList += roundEnv
//      .getElementsAnnotatedWith(instanceAnnotationClass)
//      .map { element ->
//        when (element.kind) {
//          ElementKind.INTERFACE -> LegacyInstanceProcessor.processClass(this, element as TypeElement)
//          else -> knownError("$instanceAnnotationName can only be used on interfaces")
//        }
//      }
//
//    if (roundEnv.processingOver()) {
//      RenzuGenerator(this, annotatedList, isolateForTests).generate()
//    }
//  }
}
