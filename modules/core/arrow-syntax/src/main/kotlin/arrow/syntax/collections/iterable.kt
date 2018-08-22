package arrow.syntax.collections

import arrow.core.*

fun <T> Iterable<T>.firstOption(): Option<T> = firstOrNull().toOption()

fun <T> Iterable<T>.firstOption(predicate: Predicate<T>): Option<T> = firstOrNull(predicate).toOption()

fun <A : Any, B> Iterable<A>.collect(vararg cases: PartialFunction<A, B>): List<B> =
  flatMap { value: A ->
    val f: PartialFunction<A, B> = cases.reduce { a, b -> a.orElse(b) }
    if (f.isDefinedAt(value)) listOf(f(value))
    else emptyList()
  }
