package arrow.syntax.collections

import arrow.syntax.function.toOption
import arrow.Option
import arrow.Some
import arrow.State
import arrow.map
import data.Disjunction
import data.map

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

fun <R, S, T> List<T>.stateTraverse(f: (T) -> State<S, R>): State<S, List<R>> = foldRight(State.pure(emptyList())) { i: T, accumulator: State<S, List<R>> ->
    f(i).map(accumulator, ({ head: R, tail: List<R> ->
        head prependTo tail
    }))
}

fun <S, T> List<State<S, T>>.stateSequential(): State<S, List<T>> = stateTraverse { it }