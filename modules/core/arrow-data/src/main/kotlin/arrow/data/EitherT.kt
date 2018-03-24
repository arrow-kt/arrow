package arrow.data

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

/**
 * [EitherT]`<F, A, B>` is a light wrapper on an `F<`[Either]`<A, B>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [EitherT] is a monad transformer for [Either].
 */
@higherkind
data class EitherT<F, A, B>(val value: Kind<F, Either<A, B>>) : EitherTOf<F, A, B>, EitherTKindedJ<F, A, B> {

    companion object {

        inline operator fun <reified F, A, B> invoke(value: Kind<F, Either<A, B>>): EitherT<F, A, B> = EitherT(value)

        fun <F, A, B> pure(b: B, MF: Applicative<F>): EitherT<F, A, B> = right(b, MF)

        fun <F, L, A, B> tailRecM(a: A, f: (A) -> EitherTOf<F, L, Either<A, B>>, MF: Monad<F>): EitherT<F, L, B> = MF.run {
            EitherT(tailRecM(a, {
                f(it).fix().value.map { recursionControl ->
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
        }

        fun <F, A, B> right(b: B, MF: Applicative<F>): EitherT<F, A, B> = EitherT(MF.pure(Right(b)))

        fun <F, A, B> left(a: A, MF: Applicative<F>): EitherT<F, A, B> = EitherT(MF.pure(Left(a)))

        inline fun <reified F, A, B> Applicative<F>.fromEither(value: Either<A, B>): EitherT<F, A, B> =
                EitherT(pure(value))
    }

    inline fun <C> fold(crossinline l: (A) -> C, crossinline r: (B) -> C, FF: Functor<F>): Kind<F, C> =  FF.run {
        value.map({ either -> either.fold(l, r) })
    }

    inline fun <C> flatMap(crossinline f: (B) -> EitherT<F, A, C>, MF: Monad<F>): EitherT<F, A, C> = flatMapF({ it -> f(it).value }, MF)

    inline fun <C> flatMapF(crossinline f: (B) -> Kind<F, Either<A, C>>, MF: Monad<F>): EitherT<F, A, C> = MF.run {
        EitherT(value.flatMap({ either -> either.fold({ MF.pure(Left(it)) }, { f(it) }) }))
    }

    inline fun <C> cata(crossinline l: (A) -> C, crossinline r: (B) -> C, FF: Functor<F>): Kind<F, C> = fold(l, r, FF)

    fun <C> liftF(fa: Kind<F, C>, FF: Functor<F>): EitherT<F, A, C> =  FF.run {
        EitherT(fa.map({ Right(it) }))
    }

    inline fun <C> semiflatMap(crossinline f: (B) -> Kind<F, C>, MF: Monad<F>): EitherT<F, A, C> = flatMap({ liftF(f(it), MF) }, MF)

    fun <C> map(f: (B) -> C, FF: Functor<F>): EitherT<F, A, C> = FF.run {
        EitherT(value.map({ it.map(f) }))
    }

    inline fun <C> mapLeft(crossinline f: (A) -> C, FF: Functor<F>): EitherT<F, C, B> = FF.run {
        EitherT(value.map({ it.mapLeft(f) }))
    }

    inline fun exists(crossinline p: (B) -> Boolean, FF: Functor<F>): Kind<F, Boolean> = FF.run {
        value.map({ it.exists(p) })
    }

    inline fun <C, D> transform(crossinline f: (Either<A, B>) -> Either<C, D>, FF: Functor<F>): EitherT<F, C, D> = FF.run {
        EitherT(value.map({ f(it) }))
    }

    fun <C> subflatMap(f: (B) -> Either<A, C>, FF: Functor<F>): EitherT<F, A, C> = transform({ it.flatMap(f = f) }, FF)

    fun toOptionT(FF: Functor<F>): OptionT<F, B> = FF.run {
        OptionT(value.map({ it.toOption() }))
    }

    fun combineK(y: EitherTOf<F, A, B>, MF: Monad<F>): EitherT<F, A, B> = MF.run {
        EitherT(fix().value.flatMap {
            when (it) {
                is Either.Left -> y.fix().value
                is Either.Right -> pure(it)
            }
        })
    }

    fun <C> ap(ff: EitherTOf<F, A, (B) -> C>, MF: Monad<F>): EitherT<F, A, C> = ff.fix().flatMap({ f -> map(f, MF) }, MF)
}
