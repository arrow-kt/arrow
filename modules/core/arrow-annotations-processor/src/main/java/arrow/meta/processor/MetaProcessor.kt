package arrow.meta.processor

import arrow.common.utils.AbstractProcessor
import arrow.common.utils.knownError
import arrow.meta.ast.Tree
import arrow.meta.ast.Type
import arrow.meta.encoder.MetaEncoder
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.asClassName
import java.io.File
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

abstract class MetaProcessor<A : Annotation, B : Tree>(
  private val annotations: List<KClass<A>>
) : AbstractProcessor(), MetaEncoder<B> {

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()

  override fun getSupportedAnnotationTypes(): Set<String> = annotations.map { it.java.canonicalName }.toSet()

  sealed class AnnotatedElement {
    data class Interface(val typeElement: TypeElement, val type: Type) : AnnotatedElement()
    data class Class(val typeElement: TypeElement, val type: Type) : AnnotatedElement()
  }

  abstract fun transform(annotatedElement: AnnotatedElement): FileSpec.Builder

  private val transformList = mutableListOf<FileSpec>()

  /**
   * Processor entry point
   */
  @Suppress("UNCHECKED_CAST")
  override fun onProcess(annotations: Set<TypeElement>, roundEnv: RoundEnvironment) {
    this.annotations.forEach { annotation ->
      transformList += roundEnv
        .getElementsAnnotatedWith(annotation.java)
        .flatMap { element ->
          //          try {
          val result = when (element.kind) {
            ElementKind.INTERFACE, ElementKind.CLASS -> {
              val typeEncoder = this as MetaEncoder<Type>
              val typeElement = element as TypeElement
              val encodingResult = typeEncoder.encode(typeElement)
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
          listOf(result.build())
        }
      if (roundEnv.processingOver()) {
        val generatedDir = File(this.generatedDir!!, annotation.java.simpleName).also { it.mkdirs() }
        transformList.forEach { it.writeTo(generatedDir) }
      }
    }
  }

}

