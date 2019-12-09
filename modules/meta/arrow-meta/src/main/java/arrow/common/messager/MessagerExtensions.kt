package arrow.common.messager

import me.eugeniomarletti.kotlin.processing.KotlinProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.tools.Diagnostic.Kind.ERROR
import javax.tools.Diagnostic.Kind.MANDATORY_WARNING
import javax.tools.Diagnostic.Kind.NOTE
import javax.tools.Diagnostic.Kind.WARNING

fun KotlinProcessingEnvironment.log(
  message: CharSequence,
  element: Element? = null,
  annotationMirror: AnnotationMirror? = null,
  annotationValue: AnnotationValue? = null
) = messager.printMessage(NOTE, message, element, annotationMirror, annotationValue)

fun KotlinProcessingEnvironment.logW(
  message: CharSequence,
  element: Element? = null,
  annotationMirror: AnnotationMirror? = null,
  annotationValue: AnnotationValue? = null
) = messager.printMessage(WARNING, message, element, annotationMirror, annotationValue)

fun KotlinProcessingEnvironment.logMW(
  message: CharSequence,
  element: Element? = null,
  annotationMirror: AnnotationMirror? = null,
  annotationValue: AnnotationValue? = null
) = messager.printMessage(MANDATORY_WARNING, message, element, annotationMirror, annotationValue)

fun KotlinProcessingEnvironment.logE(
  message: CharSequence,
  element: Element? = null,
  annotationMirror: AnnotationMirror? = null,
  annotationValue: AnnotationValue? = null
) = messager.printMessage(ERROR, message, element, annotationMirror, annotationValue)
