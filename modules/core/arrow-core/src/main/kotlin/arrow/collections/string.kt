package arrow.collections

import arrow.core.Option
import arrow.core.Predicate

fun String.firstOption(): Option<Char> = firstOrNull().toOption()

inline fun String.firstOption(predicate: Predicate<Char>): Option<Char> = firstOrNull(predicate).toOption()