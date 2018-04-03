package arrow.optics

/**
 * [Iso] that defines equality between String and [List] of [Char]
 */
fun String.Companion.toList(): Iso<String, List<Char>> = Iso(
  get = CharSequence::toList,
  reverseGet = { it.joinToString(separator = "") }
)
