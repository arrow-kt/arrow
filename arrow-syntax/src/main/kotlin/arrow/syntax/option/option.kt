package arrow.syntax.option

import arrow.*
import arrow.toEitherLeft
import arrow.toEitherRight

@Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
inline fun <X, T> Option<T>.toDisjunctionRight(left: () -> X): Disjunction<X, T> = toEitherRight(left).toDisjunction()

@Deprecated("arrow.data.Either is right biased. This method will be removed in future releases")
inline fun <X, T> Option<T>.toDisjunctionLeft(right: () -> X): Disjunction<T, X> = toEitherLeft(right).toDisjunction()

fun <A, G, B> Option<A>.traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Option<B>> =
        this.ev().let { option ->
            when (option) {
                is Option.Some -> GA.map(f(option.t), { Option.Some(it) })
                is Option.None -> GA.pure(None)
            }
        }

fun <A, G, B> Option<A>.traverseFilter(f: (A) -> HK<G, Option<B>>, GA: Applicative<G>): HK<G, Option<B>> =
        this.ev().let { option ->
            when (option) {
                is Option.Some -> f(option.t)
                is Option.None -> GA.pure(None)
            }
        }