package arrow.optics.plugin.internals

/**
 * From Eugenio's https://github.com/Takhion/kotlin-metadata If this [isNotBlank] then it adds the
 * optional [prefix] and [postfix].
 */
fun String.plusIfNotBlank(prefix: String = "", postfix: String = "") =
  if (isNotBlank()) "$prefix${this}$postfix" else this
