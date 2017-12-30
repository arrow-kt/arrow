package arrow.legacy

import arrow.core.*

@Deprecated("arrow.data.Either is right biased. This data type will be removed in future releases")
class LeftProjection<out L, out R>(val e: Either<L, R>) {

    fun get(): L = when (e) {
        is Either.Left -> e.a
        is Either.Right -> throw NoSuchElementException("Either.left.value on Right")
    }

    fun forEach(f: (L) -> Unit) {
        when (e) {
            is Either.Left<L, R> -> f(e.a)
        }
    }

    fun exists(predicate: (L) -> Boolean): Boolean = when (e) {
        is Either.Left -> predicate(e.a)
        is Either.Right -> false
    }

    fun <X> map(f: (L) -> X): Either<X, R> = flatMap { Left(f(it)) }

    fun filter(predicate: (L) -> Boolean): Option<Either<L, R>> = when (e) {
        is Either.Left -> {
            if (predicate(e.a)) {
                Some(e)
            } else {
                None
            }
        }
        is Either.Right -> None
    }

    fun toList(): List<L> = when (e) {
        is Either.Left -> listOf(e.a)
        is Either.Right -> listOf()
    }

    fun toOption(): Option<L> = when (e) {
        is Either.Left -> Some(e.a)
        is Either.Right -> None
    }

}

fun <L, R, X> LeftProjection<L, R>.flatMap(f: (L) -> Either<X, R>): Either<X, R> = when (e) {
    is Either.Left -> f(e.a)
    is Either.Right -> Right(e.b)
}

fun <L, R, X, Y> LeftProjection<L, R>.map(x: Either<X, R>, f: (L, X) -> Y): Either<Y, R> = flatMap { l -> x.left().map { xx -> f(l, xx) } }

fun <L, R> LeftProjection<L, R>.getOrElse(default: () -> L): L = when (e) {
    is Either.Left -> e.a
    is Either.Right -> default()
}
