package arrow.data

import arrow.*

@Deprecated("arrow.data.Either is right biased. This data type will be removed in future releases")
class LeftProjection<out L, out R>(val e: Either<L, R>) {

    fun get(): L = when (e) {
        is Left -> e.a
        is Right -> throw NoSuchElementException("Either.left.value on Right")
    }

    fun forEach(f: (L) -> Unit) {
        when (e) {
            is Left<L, R> -> f(e.a)
        }
    }

    fun exists(predicate: (L) -> Boolean): Boolean = when (e) {
        is Left -> predicate(e.a)
        is Right -> false
    }

    fun <X> map(f: (L) -> X): Either<X, R> = flatMap { Left(f(it)) }

    fun filter(predicate: (L) -> Boolean): Option<Either<L, R>> = when (e) {
        is Left -> {
            if (predicate(e.a)) {
                Some(e)
            } else {
                None
            }
        }
        is Right -> None
    }

    fun toList(): List<L> = when (e) {
        is Left -> listOf(e.a)
        is Right -> listOf()
    }

    fun toOption(): Option<L> = when (e) {
        is Left -> Some(e.a)
        is Right -> None
    }

}

fun <L, R, X> LeftProjection<L, R>.flatMap(f: (L) -> Either<X, R>): Either<X, R> = when (e) {
    is Left -> f(e.a)
    is Right -> Right(e.b)
}

fun <L, R, X, Y> LeftProjection<L, R>.map(x: Either<X, R>, f: (L, X) -> Y): Either<Y, R> = flatMap { l -> x.left().map { xx -> f(l, xx) } }

fun <L, R> LeftProjection<L, R>.getOrElse(default: () -> L): L = when (e) {
    is Left -> e.a
    is Right -> default()
}
