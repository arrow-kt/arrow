package arrow.syntax.option

import arrow.*
import arrow.syntax.either.left
import arrow.syntax.either.right

fun <T> T?.toOption(): Option<T> = if (this != null) {
    Some(this)
} else {
    None
}

fun <A> A.some(): Option<A> = Some(this)

fun <A> none(): Option<A> = None

fun <A, L> Option<A>.toEither(ifEmpty: () -> L): Either<L, A> =
        this.fold({ ifEmpty().left() }, { it.right() })

@Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
inline fun <X, T> Option<T>.toEitherRight(left: () -> X): Either<X, T> = if (isEmpty()) {
    Either.Left(left())
} else {
    Either.Right(get())
}

@Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
inline fun <X, T> Option<T>.toEitherLeft(right: () -> X): Either<T, X> = if (isEmpty()) {
    Either.Right(right())
} else {
    Either.Left(get())
}

@Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
inline fun <X, T> Option<T>.toDisjunctionRight(left: () -> X): Disjunction<X, T> = toEitherRight(left).toDisjunction()

@Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
inline fun <X, T> Option<T>.toDisjunctionLeft(right: () -> X): Disjunction<T, X> = toEitherLeft(right).toDisjunction()