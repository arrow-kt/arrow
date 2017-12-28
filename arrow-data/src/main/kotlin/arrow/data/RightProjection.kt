package arrow.data

import arrow.*

@Deprecated("arrow.data.Either is right biased. This data type will be removed in future releases")
class RightProjection<out L, out R>(val e: Either<L, R>) {

    fun get(): R = when (e) {
        is Right -> e.b
        is Left -> throw NoSuchElementException("Either.right.value on Left")
    }

    fun forEach(f: (R) -> Unit) {
        when (e) {
            is Right -> f(e.b)
        }
    }

    fun exists(predicate: (R) -> Boolean): Boolean = when (e) {
        is Right -> predicate(e.b)
        is Left -> false
    }

    fun <X> map(f: (R) -> X): Either<L, X> = flatMap { Right(f(it)) }

    fun filter(predicate: (R) -> Boolean): Option<Either<L, R>> = when (e) {
        is Right -> {
            if (predicate(e.b)) {
                Some(e)
            } else {
                None
            }
        }
        is Left -> None
    }

    fun toList(): List<R> = when (e) {
        is Right -> listOf(e.b)
        is Left -> listOf()
    }

    fun toOption(): Option<R> = when (e) {
        is Right -> Some(e.b)
        is Left -> None
    }

}

fun <L, R> RightProjection<L, R>.getOrElse(default: () -> R): R = when (e) {
    is Right -> e.b
    is Left -> default()
}

fun <X, L, R> RightProjection<L, R>.flatMap(f: (R) -> Either<L, X>): Either<L, X> = when (e) {
    is Left -> Left(e.a)
    is Right -> f(e.b)
}

fun <L, R, X, Y> RightProjection<L, R>.map(x: Either<L, X>, f: (R, X) -> Y): Either<L, Y> = flatMap { r -> x.right().map { xx -> f(r, xx) } }
