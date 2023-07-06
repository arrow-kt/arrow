package arrow.optics.plugin.internals

import com.google.devtools.ksp.symbol.KSName

/**
 * From Eugenio's https://github.com/Takhion/kotlin-metadata If this [isNotBlank] then it adds the
 * optional [prefix] and [postfix].
 */
fun String.plusIfNotBlank(prefix: String = "", postfix: String = "") =
  if (isNotBlank()) "$prefix${this}$postfix" else this

/**
 * Sanitizes each delimited section if it matches with Kotlin reserved keywords.
 */
fun KSName.asSanitizedString(delimiter: String = ".", prefix: String = "") =
  asString().splitToSequence(delimiter).joinToString(delimiter, prefix) { if (it in kotlinKeywords) "`$it`" else it }

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
