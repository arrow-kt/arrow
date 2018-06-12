package arrow.optics

import arrow.data.ListK

/**
 * [Iso] that defines equality between String and [List] of [Char]
 */
fun String.Companion.toList(): Iso<String, List<Char>> = Iso(
  get = CharSequence::toList,
  reverseGet = { it.joinToString(separator = "") }
)

/**
 * [Iso] that defines equality between String and [ListK] of [Char]
 */
fun String.Companion.toListK(): Iso<String, ListK<Char>> =
  toList() compose ListOptics.toListK()
