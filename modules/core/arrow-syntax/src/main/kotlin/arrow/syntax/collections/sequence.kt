package arrow.syntax.collections

import arrow.core.Option
import arrow.core.Predicate
import arrow.core.toOption

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun <T> Sequence<T?>.firstOption(): Option<T> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun <T> Sequence<T>.firstOption(predicate: Predicate<T>): Option<T> = firstOrNull(predicate).toOption()
