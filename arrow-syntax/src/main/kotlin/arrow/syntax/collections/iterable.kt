package arrow.syntax.collections

import arrow.syntax.function.toOption
import arrow.Option
import arrow.Predicate

fun <T> Iterable<T>.firstOption(): Option<T> = firstOrNull().toOption()

fun <T> Iterable<T>.firstOption(predicate: Predicate<T>): Option<T> = firstOrNull(predicate).toOption()
