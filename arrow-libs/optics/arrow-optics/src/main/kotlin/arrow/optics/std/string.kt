package arrow.optics

import arrow.core.ListExtensions
import arrow.core.ListK

private val stringToList: Iso<String, List<Char>> = Iso(
  get = CharSequence::toList,
  reverseGet = { it.joinToString(separator = "") }
)

/**
 * [Iso] that defines equality between String and [List] of [Char]
 */
@Deprecated(
  "The function has been moved to Iso's companion object.",
  ReplaceWith(
    "Iso.stringToList()",
    "arrow.optics.Iso"
  ),
  DeprecationLevel.WARNING
)
fun String.Companion.toList(): Iso<String, List<Char>> =
  stringToList

/**
 * [Iso] that defines equality between String and [ListK] of [Char]
 */
@Deprecated(
  "ListK is being deprecated. Use the function defined for List from Iso's companion object.",
  ReplaceWith(
    "Iso.stringToList()",
    "arrow.optics.Iso"
  ),
  DeprecationLevel.WARNING
)
fun String.Companion.toListK(): Iso<String, ListK<Char>> =
  stringToList compose ListExtensions.toListK()
