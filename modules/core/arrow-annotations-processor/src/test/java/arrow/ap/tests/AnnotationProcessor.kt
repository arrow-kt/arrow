package arrow.ap.tests

import javax.annotation.processing.Processor

data class AnnotationProcessor(
  val name: String,
  val sourceFiles: List<String>,
  val destFile: String? = null,
  val processor: Processor,
  val errorMessage: String? = null
)