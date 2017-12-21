package arrow.syntax.function

import arrow.*

fun <P1, R> ((P1) -> R).optionLift(): (Option<P1>) -> Option<R> = { it.map(this) }

fun <T> T?.toOption(): Option<T> = if (this != null) {
    Some(this)
} else {
    None
}

fun <A> A.some(): Option<A> = Some(this)

fun <A> none(): Option<A> = None

fun <A, L> Option<A>.toEither(ifEmpty: () -> L): Either<L, A> =
        this.fold({ ifEmpty().left() }, { it.right() })