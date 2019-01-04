package arrow.extensions

import arrow.common.utils.*
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

/**
 * To be removed
 */
@Deprecated(
  "LegacyInstanceProcessor is no longer the @instance processor. " +
  "The @extension annotation is now handled by the ExtensionProcessor")
class LegacyInstanceProcessor : AbstractProcessor() {

  private val annotatedList = mutableListOf<AnnotatedInstance>()

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
          ElementKind.INTERFACE -> processClass(this, element as TypeElement)
          else -> knownError("$instanceAnnotationName can only be used on interfaces")
        }
      }

    if (roundEnv.processingOver()) {
      val generatedDir = File(this.generatedDir!!, instanceAnnotationClass.simpleName).also { it.mkdirs() }
      InstanceFileGenerator(generatedDir, annotatedList, this).generate()
    }
  }

  companion object {

    private fun findNestedType(blob: String): String =
      blob.removeBackticks()
        .replace("arrow.Kind<(.*?)>".toRegex(), "$1")
        .substringAfter("<")
        .substringBeforeLast(">")
        .substringBefore("<")
        .replace(".For", ".")
        .removeSuffix("PartialOf")
        .removeSuffix("Of")
        .substringBefore(",")

    fun processClass(processor: AbstractProcessor, element: TypeElement): AnnotatedInstance {
      val proto: ClassOrPackageDataWrapper.Class = processor.getClassOrPackageDataWrapper(element) as ClassOrPackageDataWrapper.Class
      val typeTable = TypeTable(proto.classProto.typeTable)
      val superTypes: List<ClassOrPackageDataWrapper.Class> =
        processor.supertypes(proto, typeTable, processor, emptyList()).map { it as ClassOrPackageDataWrapper.Class }
      val typeClass = if (superTypes.isEmpty()) {
        knownError("@instance `${proto.fullName}` needs to extend a type class (interface with one type parameter) as it's first interface in the `extends` declaration")
      } else superTypes[0]
      val dataType: ClassOrPackageDataWrapper.Class = if (typeClass.typeParameters.isEmpty()) {
        knownError("@instance `${proto.fullName}` needs to extend a type class (interface with one type parameter) as it's first interface in the `extends` declaration")
      } else {
        val name = proto.classProto.getSupertype(0).extractFullName(proto)
        val target = findNestedType(name)
        val dataTypeElement = processor.elementUtils.getTypeElement(target)
          ?: knownError("found null datatype element on `${proto.fullName}` for $name, targeted at -> $target. `${proto.fullName}` needs to extend a type class (interface with one type parameter) as it's first interface in the `extends` declaration)")
        processor.getClassOrPackageDataWrapper(dataTypeElement) as ClassOrPackageDataWrapper.Class
      }
      return AnnotatedInstance(
        instance = element,
        dataTypeInstance = proto,
        superTypes = superTypes,
        processor = processor,
        dataType = dataType,
        typeClass = typeClass
      )
    }
  }

}