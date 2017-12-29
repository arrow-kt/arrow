package arrow.syntax.collections

import arrow.Option
import arrow.Predicate
import arrow.syntax.option.*

fun String.firstOption(): Option<Char> = firstOrNull().toOption()

inline fun String.firstOption(predicate: Predicate<Char>): Option<Char> = firstOrNull(predicate).toOption()