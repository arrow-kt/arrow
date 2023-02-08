package arrow.optics.plugin.internals

/**
 * From Eugenio's https://github.com/Takhion/kotlin-metadata If this [isNotBlank] then it adds the
 * optional [prefix] and [postfix].
 */
fun String.plusIfNotBlank(prefix: String = "", postfix: String = "") =
  if (isNotBlank()) "$prefix${this}$postfix" else this

/**
 * Sanitizes each delimited section if it matches with Kotlin reserved keywords.
 */
fun String.sanitizeDelimited(delimiter: String = ".", separator: String = delimiter) =
  if (isNotBlank() && contains(delimiter)) {
    val keywords = setOf("as", "by", "do", "in", "is", "it")
    splitToSequence(delimiter).joinToString(separator) { if (keywords.contains(it)) "`$it`" else it }
  } else this
