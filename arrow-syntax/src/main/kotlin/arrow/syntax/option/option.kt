package arrow.syntax.option

import arrow.*
import arrow.toEitherLeft
import arrow.toEitherRight

@Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
inline fun <X, T> Option<T>.toDisjunctionRight(left: () -> X): Disjunction<X, T> = toEitherRight(left).toDisjunction()

@Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
inline fun <X, T> Option<T>.toDisjunctionLeft(right: () -> X): Disjunction<T, X> = toEitherLeft(right).toDisjunction()