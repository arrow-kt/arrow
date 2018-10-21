package arrow.ank

import org.gradle.api.file.FileCollection
import java.io.File

data class AnkExtension(
  var source: File? = null,
  var target: File? = null,
  var classpath: FileCollection? = null)