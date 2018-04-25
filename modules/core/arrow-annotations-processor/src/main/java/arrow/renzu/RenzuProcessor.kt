package arrow.renzu

import arrow.common.utils.AbstractProcessor
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.knownError
import arrow.instances.AnnotatedInstance
import arrow.instances.instanceAnnotationClass
import arrow.instances.instanceAnnotationName
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.serialization.deserialization.TypeTable
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class RenzuProcessor : AbstractProcessor() {

  val annotatedList = mutableListOf<AnnotatedInstance>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes(): Set<String> = setOf(instanceAnnotationClass.canonicalName)

  /**
   * Processor entry point
   */
  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    annotatedList += roundEnv
      .getElementsAnnotatedWith(instanceAnnotationClass)
      .map { element ->
        when (element.kind) {
          ElementKind.INTERFACE -> processClass(element as TypeElement)
          else -> knownError("$instanceAnnotationName can only be used on interfaces")
        }
      }

    if (roundEnv.processingOver()) {
      val generatedDir = File("./infographic/", "").also { it.mkdirs() }
      RenzuGenerator(this, generatedDir, annotatedList).generate()
    }
  }

  private fun processClass(element: TypeElement): AnnotatedInstance {
    val proto: ClassOrPackageDataWrapper.Class = getClassOrPackageDataWrapper(element) as ClassOrPackageDataWrapper.Class
    val dataType = element.annotationMirrors.flatMap { am ->
      am.elementValues.entries.filter {
        "target" == it.key.simpleName.toString()
      }.map {
        val targetName = it.value.toString().replace(".class", "")
        val targetElement = elementUtils.getTypeElement(targetName)
        getClassOrPackageDataWrapper(targetElement) as ClassOrPackageDataWrapper.Class
      }
    }
    val typeTable = TypeTable(proto.classProto.typeTable)
    val superTypes: List<ClassOrPackageDataWrapper.Class> =
      recurseTypeclassInterfaces(proto, typeTable, emptyList()).map { it as ClassOrPackageDataWrapper.Class }
    return AnnotatedInstance(element, proto, superTypes, this, dataType[0])
  }
}
