package arrow.common.messager

import me.eugeniomarletti.kotlin.processing.KotlinProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.tools.Diagnostic.Kind.ERROR
import javax.tools.Diagnostic.Kind.MANDATORY_WARNING
import javax.tools.Diagnostic.Kind.NOTE
import javax.tools.Diagnostic.Kind.WARNING

public fun KotlinProcessingEnvironment.log(
  message: CharSequence,
  element: Element? = null,
  annotationMirror: AnnotationMirror? = null,
  annotationValue: AnnotationValue? = null
): Unit = messager.printMessage(NOTE, message, element, annotationMirror, annotationValue)

public fun KotlinProcessingEnvironment.logW(
  message: CharSequence,
  element: Element? = null,
  annotationMirror: AnnotationMirror? = null,
  annotationValue: AnnotationValue? = null
): Unit = messager.printMessage(WARNING, message, element, annotationMirror, annotationValue)

public fun KotlinProcessingEnvironment.logMW(
  message: CharSequence,
  element: Element? = null,
  annotationMirror: AnnotationMirror? = null,
  annotationValue: AnnotationValue? = null
): Unit = messager.printMessage(MANDATORY_WARNING, message, element, annotationMirror, annotationValue)

public fun KotlinProcessingEnvironment.logE(
  message: CharSequence,
  element: Element? = null,
  annotationMirror: AnnotationMirror? = null,
  annotationValue: AnnotationValue? = null
): Unit = messager.printMessage(ERROR, message, element, annotationMirror, annotationValue)
