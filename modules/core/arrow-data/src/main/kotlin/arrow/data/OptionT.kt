package arrow.data

import arrow.*
import arrow.core.*
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.applicative

/**
 * [OptionT]`<F, A>` is a light wrapper on an `F<`[Option]`<A>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [OptionT] is a monad transformer for [Option].
 */
@higherkind data class OptionT<F, A>(val value: Kind<F, Option<A>>) : OptionTOf<F, A>, OptionTKindedJ<F, A> {

    companion object {

        operator fun <F, A> invoke(value: Kind<F, Option<A>>): OptionT<F, A> = OptionT(value)

        inline fun <reified F, A> pure(a: A, AF: Applicative<F> = applicative<F>()): OptionT<F, A> = OptionT(AF.pure(Some(a)))

        inline fun <reified F> none(AF: Applicative<F> = applicative<F>()): OptionT<F, Nothing> = OptionT(AF.pure(None))

        inline fun <reified F, A> fromOption(value: Option<A>, AF: Applicative<F> = applicative<F>()): OptionT<F, A> =
                OptionT(AF.pure(value))

        fun <F, A, B> tailRecM(a: A, f: (A) -> OptionTOf<F, Either<A, B>>, MF: Monad<F>): OptionT<F, B> =
                OptionT(MF.tailRecM(a, {
                    MF.map(f(it).extract().value, {
                        it.fold({
                            Right<Option<B>>(None)
                        }, {
                            it.map { Some(it) }
                        })
                    })
                }))

    }

    inline fun <B> fold(crossinline default: () -> B, crossinline f: (A) -> B, FF: Functor<F>): Kind<F, B> = FF.map(value, { option -> option.fold(default, f) })

    inline fun <B> cata(crossinline default: () -> B, crossinline f: (A) -> B, FF: Functor<F>): Kind<F, B> = fold(default, f, FF)

    fun <B> ap(ff: OptionTOf<F, (A) -> B>, MF: Monad<F>): OptionT<F, B> = ff.extract().flatMap({ f -> map(f, MF) }, MF)

    inline fun <B> flatMap(crossinline f: (A) -> OptionT<F, B>, MF: Monad<F>): OptionT<F, B> = flatMapF({ it -> f(it).value }, MF)

    inline fun <B> flatMapF(crossinline f: (A) -> Kind<F, Option<B>>, MF: Monad<F>): OptionT<F, B> =
            OptionT(MF.flatMap(value, { option -> option.fold({ MF.pure(None) }, f) }))

    fun <B> liftF(fa: Kind<F, B>, FF: Functor<F>): OptionT<F, B> = OptionT(FF.map(fa, { Some(it) }))

    inline fun <B> semiflatMap(crossinline f: (A) -> Kind<F, B>, MF: Monad<F>): OptionT<F, B> = flatMap({ option -> liftF(f(option), MF) }, MF)

    inline fun <B> map(crossinline f: (A) -> B, FF: Functor<F>): OptionT<F, B> = OptionT(FF.map(value, { it.map(f) }))

    fun getOrElse(default: () -> A, FF: Functor<F>): Kind<F, A> = FF.map(value, { it.getOrElse(default) })

    inline fun getOrElseF(crossinline default: () -> Kind<F, A>, MF: Monad<F>): Kind<F, A> = MF.flatMap(value, { it.fold(default, { MF.pure(it) }) })

    inline fun filter(crossinline p: (A) -> Boolean, FF: Functor<F>): OptionT<F, A> = OptionT(FF.map(value, { it.filter(p) }))

    inline fun forall(crossinline p: (A) -> Boolean, FF: Functor<F>): Kind<F, Boolean> = FF.map(value, { it.forall(p) })

    fun isDefined(FF: Functor<F>): Kind<F, Boolean> = FF.map(value, { it.isDefined() })

    fun isEmpty(FF: Functor<F>): Kind<F, Boolean> = FF.map(value, { it.isEmpty() })

    inline fun orElse(crossinline default: () -> OptionT<F, A>, MF: Monad<F>): OptionT<F, A> = orElseF({ default().value }, MF)

    inline fun orElseF(crossinline default: () -> Kind<F, Option<A>>, MF: Monad<F>): OptionT<F, A> =
            OptionT(MF.flatMap(value) {
                when (it) {
                    is Some<A> -> MF.pure(it)
                    is None -> default()
                }
            })

    inline fun <B> transform(crossinline f: (Option<A>) -> Option<B>, FF: Functor<F>): OptionT<F, B> = OptionT(FF.map(value, { f(it) }))

    inline fun <B> subflatMap(crossinline f: (A) -> Option<B>, FF: Functor<F>): OptionT<F, B> = transform({ it.flatMap(f) }, FF)

    fun <R> toLeft(default: () -> R, FF: Functor<F>): EitherT<F, A, R> =
            EitherT(cata({ Right(default()) }, { Left(it) }, FF))

    fun <L> toRight(default: () -> L, FF: Functor<F>): EitherT<F, L, A> =
            EitherT(cata({ Left(default()) }, { Right(it) }, FF))
}

inline fun <F, A, B> OptionT<F, A>.mapFilter(crossinline f: (A) -> Option<B>, FF: Functor<F>): OptionT<F, B> =
        OptionT(FF.map(value, { it.flatMap(f) }))

fun <F, A> OptionTOf<F, A>.value(): Kind<F, Option<A>> = this.extract().value