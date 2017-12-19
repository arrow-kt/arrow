package arrow.syntax.collections

import arrow.syntax.function.toOption
import kategory.Option

fun String.firstOption(): Option<Char> = firstOrNull().toOption()

inline fun String.firstOption(predicate: (Char) -> Boolean): Option<Char> = firstOrNull(predicate).toOption()