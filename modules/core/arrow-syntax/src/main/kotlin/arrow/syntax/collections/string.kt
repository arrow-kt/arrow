package arrow.syntax.collections

import arrow.core.Option
import arrow.core.Predicate
import arrow.core.toOption

fun String.firstOption(): Option<Char> = firstOrNull().toOption()

inline fun String.firstOption(predicate: Predicate<Char>): Option<Char> = firstOrNull(predicate).toOption()
