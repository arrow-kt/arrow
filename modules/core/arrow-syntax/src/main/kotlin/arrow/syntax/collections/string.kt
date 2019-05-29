package arrow.syntax.collections

import arrow.core.Option
import arrow.core.Predicate
import arrow.core.toOption

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
fun String.firstOption(): Option<Char> = firstOrNull().toOption()

@Deprecated(message = "`firstOption` is now part of the Foldable interface and generalized to all foldable data types")
inline fun String.firstOption(predicate: Predicate<Char>): Option<Char> = firstOrNull(predicate).toOption()
