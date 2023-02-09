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
    splitToSequence(delimiter).joinToString(separator) { if (kotlinKeywords.contains(it)) "`$it`" else it }
  } else this


private val kotlinKeywords = setOf(
  // Hard keywords
  "as",
  "break",
  "class",
  "continue",
  "do",
  "else",
  "false",
  "for",
  "fun",
  "if",
  "in",
  "interface",
  "is",
  "null",
  "object",
  "package",
  "return",
  "super",
  "this",
  "throw",
  "true",
  "try",
  "typealias",
  "typeof",
  "val",
  "var",
  "when",
  "while",

  // Soft keywords
  "by",
  "catch",
  "constructor",
  "delegate",
  "dynamic",
  "field",
  "file",
  "finally",
  "get",
  "import",
  "init",
  "param",
  "property",
  "receiver",
  "set",
  "setparam",
  "where",

  // Modifier keywords
  "actual",
  "abstract",
  "annotation",
  "companion",
  "const",
  "crossinline",
  "data",
  "enum",
  "expect",
  "external",
  "final",
  "infix",
  "inline",
  "inner",
  "internal",
  "lateinit",
  "noinline",
  "open",
  "operator",
  "out",
  "override",
  "private",
  "protected",
  "public",
  "reified",
  "sealed",
  "suspend",
  "tailrec",
  "value",
  "vararg",

  // These aren't keywords anymore but still break some code if unescaped.
  // https://youtrack.jetbrains.com/issue/KT-52315
  "header",
  "impl",

  // Other reserved keywords
  "yield",
)
