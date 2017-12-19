package arrow.syntax.collections

import arrow.syntax.function.toOption
import kategory.Option
import kategory.Predicate

fun <T> Sequence<T?>.firstOption(): Option<T> = firstOrNull().toOption()

fun <T> Sequence<T>.firstOption(predicate: Predicate<T>): Option<T> = firstOrNull(predicate).toOption()