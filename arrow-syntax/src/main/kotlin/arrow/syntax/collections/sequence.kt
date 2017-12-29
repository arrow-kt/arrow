package arrow.syntax.collections

import arrow.syntax.option.*
import arrow.Option
import arrow.Predicate

fun <T> Sequence<T?>.firstOption(): Option<T> = firstOrNull().toOption()

fun <T> Sequence<T>.firstOption(predicate: Predicate<T>): Option<T> = firstOrNull(predicate).toOption()