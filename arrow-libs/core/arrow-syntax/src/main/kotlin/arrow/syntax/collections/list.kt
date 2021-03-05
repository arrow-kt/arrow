package arrow.syntax.collections

import arrow.core.Option
import arrow.core.tail as _tail
import arrow.core.flatten as _flatten
import arrow.core.destructured as _destructured
import arrow.core.prependTo as _prependTo

/**
 * Returns a list containing all elements except the first element
 */
@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("tail()", "arrow.core.tail")
)
fun <T> List<T>.tail(): List<T> =
  _tail()

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("prependTo(list)", "arrow.core.prependTo")
)
infix fun <T> T.prependTo(list: List<T>): List<T> =
  _prependTo(list)

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("destructured()", "arrow.core.destructured")
)
fun <T> List<T>.destructured(): Pair<T, List<T>> =
  _destructured()

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("flatten()", "arrow.core.flatten")
)
fun <T> List<Option<T>>.flatten(): List<T> =
  _flatten()
