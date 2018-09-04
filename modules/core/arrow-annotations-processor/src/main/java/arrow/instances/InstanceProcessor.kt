package arrow.instances

import arrow.common.utils.*
import arrow.extension
import arrow.meta.ast.*
import arrow.meta.decoder.TypeDecoder
import arrow.meta.encoder.instances.TypeEncoder
import arrow.meta.processor.MetaProcessor
import arrow.meta.processor.MetaProcessorUtils
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.FileSpec
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class InstanceProcessor : MetaProcessor<extension, Type>(annotations = listOf(extension::class)), TypeEncoder, TypeDecoder {
  override fun transform(annotatedElement: AnnotatedElement): FileSpec.Builder =
    when (annotatedElement) {
      is AnnotatedElement.Interface -> {
        val info = annotatedElement.typeElement.typeClassInstance(this)
        info?.genDataTypeExtensions()?.fold(annotatedElement.fileSpec) { spec, func ->
          val cleaned = func.removeConstrains().downKindParameters()
          spec.addFunction(cleaned.lyrics())
        } ?: annotatedElement.fileSpec.addComment("Not Processes by Instance Type Class Generator")
      }
      else -> knownError("@instance is only allowed on `interface` extending another interface of at least one type argument (type class) as first declaration in the extension list")
    }

  //  override fun transform(annotatedElement: AnnotatedElement): FileSpec.Builder =

//
//
  fun MetaProcessorUtils.TypeClassInstance.genDataTypeExtensions(): List<Func> {
    val extensionSet = typeClass.declaredFunctions.map { it.jvmMethodSignature }
    return instance.allFunctions
      .filter { extensionSet.contains(it.jvmMethodSignature) }
  }
}

//@AutoService(Processor::class)
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
      InstanceFileGenerator(generatedDir, annotatedList).generate()
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
        processor.supertypes(proto, typeTable, emptyList()).map { it as ClassOrPackageDataWrapper.Class }
      val typeClass = if (superTypes.isEmpty()) {
        knownError("@extension `${proto.fullName}` needs to extend a type class (interface with one type parameter) as it's first interface in the `extends` declaration")
      } else superTypes[0]
      val dataType: ClassOrPackageDataWrapper.Class = if (typeClass.typeParameters.isEmpty()) {
        knownError("@extension `${proto.fullName}` needs to extend a type class (interface with one type parameter) as it's first interface in the `extends` declaration")
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