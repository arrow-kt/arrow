package arrow.meta.processor

import aballano.kotlinmemoization.memoize
import arrow.common.utils.AbstractProcessor
import arrow.common.utils.ClassOrPackageDataWrapper
import arrow.common.utils.ProcessorUtils
import arrow.common.utils.knownError
import arrow.meta.ast.Type
import arrow.meta.ast.TypeName
import arrow.meta.encoder.jvm.JvmMetaApi
import arrow.meta.processor.MetaProcessor.AnnotatedElement
import com.squareup.kotlinpoet.FileSpec
import me.eugeniomarletti.kotlin.metadata.KotlinMetadataUtils
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

/**
 * The Meta Processor provides access to the Meta Api and is meant to be extended by concrete processors.
 * It performs processing automatically provided the concrete processor implements:
 *
 * override fun transform(annotatedElement: AnnotatedElement): FileSpec.Builder
 *
 * [AnnotatedElement] provides already reified `Type` instances from the Arrow meta AST
 * that attempts to unify as much info as possible from the annotated Kotlin code.
 *
 * The Current [JvmMetaApi] impl includes support for extracting information with a blend
 * of Kotlin Poet, TypeElement java api's and the Kotlin Metadata Library.
 */
abstract class MetaProcessor<A : Annotation>(private val annotation: KClass<A>) : AbstractProcessor(), JvmMetaApi {

  override fun processorUtils(): ProcessorUtils = this

  override fun kotlinMetadataUtils(): KotlinMetadataUtils = this

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes(): Set<String> = setOf(annotation.java.canonicalName)

  override val typeNameDownKind: (typeName: TypeName) -> TypeName =
    ::typeNameDownKindImpl.memoize()

  override val typeElementToMeta: (classElement: TypeElement) -> ClassOrPackageDataWrapper =
    ::getClassOrPackageDataWrapper.memoize()

  override val typeNameToMeta: (typeName: com.squareup.kotlinpoet.TypeName) -> TypeName =
    ::typeNameToMetaImpl.memoize()

  sealed class AnnotatedElement {
    data class Interface(val typeElement: TypeElement, val type: Type) : AnnotatedElement()
    data class Class(val typeElement: TypeElement, val type: Type) : AnnotatedElement()
  }

  abstract fun transform(annotatedElement: AnnotatedElement): List<FileSpec.Builder>

  private val transformList = mutableListOf<FileSpec>()

  /**
   * Processor entry point
   */
  @Suppress("UNCHECKED_CAST")
  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
      transformList += roundEnv
        .getElementsAnnotatedWith(annotation.java)
        .flatMap { element ->
          //          try {
          val result = when (element.kind) {
            ElementKind.INTERFACE, ElementKind.CLASS -> {
              val typeElement = element as TypeElement
              val encodingResult = typeElement.type()
              encodingResult.fold({ knownError(it.toString()) }, {
                transform(
                  if (element.kind.isInterface)
                    AnnotatedElement.Interface(
                      typeElement = typeElement,
                      type = it
                    )
                  else AnnotatedElement.Class(
                    typeElement = typeElement,
                    type = it
                  ))
              })
            }
            else -> knownError("Unsupported meta annotation: $annotation over ${element.kind.name} ")
          }
          result.map { it.build() }
        }
      if (roundEnv.processingOver()) {
        val generatedDir = File(this.generatedDir!!, annotation.java.simpleName).also { it.mkdirs() }
        transformList.forEach { it.writeTo(generatedDir) }
      }
  }

}

