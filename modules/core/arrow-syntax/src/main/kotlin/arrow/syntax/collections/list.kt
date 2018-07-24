package arrow.syntax.collections

import arrow.core.Option
import arrow.core.Some
import arrow.core.identity
import arrow.core.toOption

/**
 * Returns a list containing all elements except the first element
 */
fun <T> List<T>.tail(): List<T> = this.drop(1)

infix fun <T> T.prependTo(list: List<T>): List<T> = listOf(this) + list

fun <T> List<T>.destructured(): Pair<T, List<T>> = first() to tail()

fun <T, R> List<T>.optionTraverse(f: (T) -> Option<R>): Option<List<R>> = foldRight(Some(emptyList())) { i: T, accumulator: Option<List<R>> ->
  f(i).map(accumulator) { head: R, tail: List<R> ->
    head prependTo tail
  }
}

fun <T> List<Option<T>>.optionSequential(): Option<List<T>> = optionTraverse(::identity)

fun <T> List<T>.firstOption(): Option<T> = firstOrNull().toOption()

fun <T> List<Option<T>>.flatten(): List<T> = filter { it.isDefined() }.map { it.get() }

