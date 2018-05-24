package arrow.optics

import arrow.common.utils.AbstractProcessor
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.extractFullName
import arrow.common.utils.isSealed
import arrow.optics.OpticsTarget.*
import arrow.optics.OpticsProcessor.Type.*
import arrow.common.utils.knownError
import arrow.common.utils.nextGenericParam
import arrow.common.utils.removeBackticks
import com.google.auto.service.AutoService
import me.eugeniomarletti.kotlin.metadata.KotlinClassMetadata
import me.eugeniomarletti.kotlin.metadata.isDataClass
import me.eugeniomarletti.kotlin.metadata.jvm.getJvmMethodSignature
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.ProtoBuf
import me.eugeniomarletti.kotlin.metadata.shadow.metadata.deserialization.hasReceiver
import me.eugeniomarletti.kotlin.metadata.shadow.serialization.deserialization.getName
import java.io.File
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement

@AutoService(Processor::class)
class OpticsProcessor : AbstractProcessor() {

  private val annotatedTypes = mutableListOf<AnnotatedType>()

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes() = setOf(opticsAnnotationClass.canonicalName)

  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    roundEnv
      .getElementsAnnotatedWith(opticsAnnotationClass)
      .forEach { element ->
        when (element.type) {
          SealedClass -> (element as TypeElement).toAnnotatedSumType()
          DataClass -> (element as TypeElement).toAnnotatedProductType()
          Other -> knownError(element.otherClassTypeErrorMessage, element)
        }.let(annotatedTypes::add)
      }

    if (roundEnv.processingOver()) {
      val generatedDir = File(this.generatedDir!!, "optics").also { it.mkdirs() }

      (annotatedTypes.filterIsInstance<AnnotatedSumType>() + annotatedTypes.filterIsInstance<AnnotatedProductType>()).forEach { ele ->
        val content = ele.snippet().asFileText(ele.packageName)
        File(generatedDir, "optics.arrow.${ele.sourceName}.kt").writeText(content)
      }
        }
    }
  }

  private fun AnnotatedType.snippet(): Snippet {
    fun AnnotatedType.normalizedTargets(): List<OpticsTarget> = with(element.getAnnotation(opticsAnnotationClass).targets) {
      when {
        isEmpty() -> when (element.type) {
          SealedClass -> listOf(PRISM, DSL)
          DataClass -> listOf(ISO, LENS, OPTIONAL, DSL)
          Other -> knownError(element.otherClassTypeErrorMessage, element)
        }
        else -> toList()
      }
    }

    return normalizedTargets().map { target ->
      when (target) {
        ISO -> isoSnippet
        LENS -> lensSnippet
        OPTIONAL -> optionalSnippet
        PRISM -> prismSnippet
        DSL -> dslSnippet
      }
    }.fold(Snippet.EMPTY, Snippet::plus)
  }

  private fun TypeElement.toAnnotatedSumType() = AnnotatedSumType(this, getClassData(), kotlinMetadata.let { it as KotlinClassMetadata }.data.let { (nameResolver, classProto) ->
    classProto.sealedSubclassFqNameList
      .map(nameResolver::getString)
      .map { it.replace('/', '.') }
      .map { Focus(it, it.substringAfterLast(".").decapitalize()) }
  }).also { if (hasNoCompanion) knownError("${opticsAnnotationClass.canonicalName} annotated class $this needs to declare companion object.", this) }



  }

  private enum class Type {
    DataClass,
    SealedClass,
    Other;
  }

  private val Element.type: Type
    get() = when {
      (kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isDataClass == true -> DataClass
      (kotlinMetadata as? KotlinClassMetadata)?.data?.classProto?.isSealed == true -> SealedClass
      else -> Other
    }

}
