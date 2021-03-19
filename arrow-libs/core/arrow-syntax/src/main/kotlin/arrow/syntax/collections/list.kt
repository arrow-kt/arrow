package arrow.syntax.collections

import arrow.core.Option
import arrow.core.tail as _tail
import arrow.core.filterOption
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
  "Unsafe operation use first and tail directly",
  ReplaceWith("Pair(this.first(), this.tail())", "arrow.core.tail")
)
fun <T> List<T>.destructured(): Pair<T, List<T>> =
  Pair(this.first(), this._tail())

@Deprecated(
  "arrow.syntax.collections package is deprecated. Use arrow.core package instead.",
  ReplaceWith("filterOption()", "arrow.core.filterOption")
)
fun <T> List<Option<T>>.flatten(): List<T> =
  filterOption()
