package arrow.derive

import arrow.common.utils.AbstractProcessor
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.knownError
import com.google.auto.service.AutoService
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.TypeTable
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class DerivingProcessor : AbstractProcessor() {

  private val annotatedList: MutableList<AnnotatedDeriving> = mutableListOf()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes(): Set<String> = setOf(derivingAnnotationClass.canonicalName)

  /**
   * Processor entry point
   */
  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    annotatedList += roundEnv
      .getElementsAnnotatedWith(derivingAnnotationClass)
      .map { element ->
        when (element.kind) {
          ElementKind.CLASS -> processClass(element as TypeElement)
          else -> knownError("$derivingAnnotationName can only be used on classes")
        }
      }

    if (roundEnv.processingOver()) {
      val generatedDir = File(this.generatedDir!!, derivingAnnotationClass.simpleName).also { it.mkdirs() }
      DerivingFileGenerator(generatedDir, annotatedList).generate()
    }
  }

  private fun processClass(element: TypeElement): AnnotatedDeriving {
    val proto: ClassOrPackageDataWrapper.Class = getClassOrPackageDataWrapper(element) as ClassOrPackageDataWrapper.Class
    val typeClasses: List<ClassOrPackageDataWrapper> = element.annotationMirrors.flatMap { am ->
      am.elementValues.entries.filter {
        "typeclasses" == it.key.simpleName.toString()
      }.flatMap {
        val l = it.value.value as List<*>
        l.map {
          val typeclassName = it.toString().replace(".class", "")
          val typeClassElement = elementUtils.getTypeElement(typeclassName)
          getClassOrPackageDataWrapper(typeClassElement)
        }
      }
    }
    val typeclassSuperTypes = typeClasses.map { tc ->
      val typeClassWrapper = tc as ClassOrPackageDataWrapper.Class
      val typeTable = TypeTable(typeClassWrapper.classProto.typeTable)
      val superTypes = supertypes(typeClassWrapper, typeTable, this, emptyList())
      typeClassWrapper to superTypes
    }.toMap()
    val className = proto.nameResolver.getString(proto.classProto.fqName).replace("/", ".")
    val companionName = className + "." +
      proto.nameResolver.getString(proto.classProto.companionObjectName)
    val typeClassElement = elementUtils.getTypeElement(companionName)
      ?: knownError("Deriving for $className: It is required that the class declares a companion object")
    val companionProto = getClassOrPackageDataWrapper(typeClassElement)
    return AnnotatedDeriving(element, proto, companionProto, typeClasses, typeclassSuperTypes)
  }

}
