package arrow.data

import arrow.*
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.monad

/**
 * [EitherT]`<F, A, B>` is a light wrapper on an `F<`[Either]`<A, B>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [EitherT] is a monad transformer for [Either].
 */
@higherkind data class EitherT<F, A, B>(val value: HK<F, Either<A, B>>) : EitherTKind<F, A, B>, EitherTKindedJ<F, A, B> {

    companion object {

        inline operator fun <reified F, A, B> invoke(value: HK<F, Either<A, B>>): EitherT<F, A, B> = EitherT(value)

        fun <F, A, B> pure(b: B, MF: Applicative<F>): EitherT<F, A, B> = right(b, MF)

        fun <F, L, A, B> tailRecM(a: A, f: (A) -> EitherTKind<F, L, Either<A, B>>, MF: Monad<F>): EitherT<F, L, B> =
                EitherT(MF.tailRecM(a, {
                    MF.map(f(it).ev().value) { recursionControl ->
                        when (recursionControl) {
                            is Either.Left<L, Either<A, B>> -> Right(Left(recursionControl.a))
                            is Either.Right<L, Either<A, B>> -> {
                                val b: Either<A, B> = recursionControl.b
                                when (b) {
                                    is Either.Left<A, B> -> Left(b.a)
                                    is Either.Right<A, B> -> Right(Right(b.b))
                                }
                            }
                        }
                    }
                }))

        fun <F, A, B> right(b: B, MF: Applicative<F>): EitherT<F, A, B> = EitherT(MF.pure(Right(b)))

        fun <F, A, B> left(a: A, MF: Applicative<F>): EitherT<F, A, B> = EitherT(MF.pure(Left(a)))

        inline fun <reified F, A, B> fromEither(value: Either<A, B>, MF: Applicative<F> = monad<F>()): EitherT<F, A, B> =
                EitherT(MF.pure(value))
    }

    inline fun <C> fold(crossinline l: (A) -> C, crossinline r: (B) -> C, FF: Functor<F>): HK<F, C> = FF.map(value, { either -> either.fold(l, r) })

    inline fun <C> flatMap(crossinline f: (B) -> EitherT<F, A, C>, MF: Monad<F>): EitherT<F, A, C> = flatMapF({ it -> f(it).value }, MF)

    inline fun <C> flatMapF(crossinline f: (B) -> HK<F, Either<A, C>>, MF: Monad<F>): EitherT<F, A, C> =
            EitherT(MF.flatMap(value, { either -> either.fold({ MF.pure(Left(it)) }, { f(it) }) }))

    inline fun <C> cata(crossinline l: (A) -> C, crossinline r: (B) -> C, FF: Functor<F>): HK<F, C> = fold(l, r, FF)

    fun <C> liftF(fa: HK<F, C>, FF: Functor<F>): EitherT<F, A, C> = EitherT(FF.map(fa, { Right(it) }))

    inline fun <C> semiflatMap(crossinline f: (B) -> HK<F, C>, MF: Monad<F>): EitherT<F, A, C> = flatMap({ liftF(f(it), MF) }, MF)

    fun <C> map(f: (B) -> C, FF: Functor<F>): EitherT<F, A, C> = EitherT(FF.map(value, { it.map(f) }))

    inline fun <C> mapLeft(crossinline f: (A) -> C, FF: Functor<F>): EitherT<F, C, B> = EitherT(FF.map(value, { it.mapLeft(f) }))

    inline fun exists(crossinline p: (B) -> Boolean, FF: Functor<F>): HK<F, Boolean> = FF.map(value, { it.exists(p) })

    inline fun <C, D> transform(crossinline f: (Either<A, B>) -> Either<C, D>, FF: Functor<F>): EitherT<F, C, D> = EitherT(FF.map(value, { f(it) }))

    fun <C> subflatMap(f: (B) -> Either<A, C>, FF: Functor<F>): EitherT<F, A, C> = transform({ it.flatMap(f) }, FF)

    fun toOptionT(FF: Functor<F>): OptionT<F, B> = OptionT(FF.map(value, { it.toOption() }))

    fun combineK(y: EitherTKind<F, A, B>, MF: Monad<F>): EitherT<F, A, B> =
            EitherT(MF.flatMap(this.ev().value) {
                when (it) {
                    is Either.Left -> y.ev().value
                    is Either.Right -> MF.pure(it)
                }
            })

    fun <C> ap(ff: EitherTKind<F, A, (B) -> C>, MF: Monad<F>): EitherT<F, A, C> = ff.ev().flatMap ({ f -> map(f, MF) }, MF)
}

fun <F, A, B> EitherTKind<F, A, B>.value(): HK<F, Either<A, B>> = this.ev().value
