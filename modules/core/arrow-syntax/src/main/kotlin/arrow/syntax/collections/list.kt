package arrow.syntax.collections

import arrow.core.Option
import arrow.core.Some
import arrow.core.toOption
import arrow.legacy.Disjunction
import arrow.legacy.map

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

fun <T> List<Option<T>>.optionSequential(): Option<List<T>> = optionTraverse { it }

fun <T> List<T>.firstOption(): Option<T> = firstOrNull().toOption()

fun <T> List<Option<T>>.flatten(): List<T> = filter { it.isDefined() }.map { it.get() }

@Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
fun <T, L, R> List<T>.disjuntionTraverse(f: (T) -> Disjunction<L, R>): Disjunction<L, List<R>> = foldRight(Disjunction.Right(emptyList())) { i: T, accumulator: Disjunction<L, List<R>> ->
    val disjunction = f(i)
    when (disjunction) {
        is Disjunction.Right -> disjunction.map(accumulator) { head: R, tail: List<R> ->
            head prependTo tail
        }
        is Disjunction.Left -> Disjunction.Left(disjunction.value)
    }
}

fun <L, R> List<Disjunction<L, R>>.disjunctionSequential(): Disjunction<L, List<R>> = disjuntionTraverse { it }
