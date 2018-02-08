package arrow.data

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import arrow.higherkind
import arrow.typeclasses.*

@Suppress("UNCHECKED_CAST") inline fun <F, W, A> WriterTOf<F, W, A>.value(): Kind<F, Tuple2<W, A>> = this.reify().value

@higherkind data class WriterT<F, W, A>(val value: Kind<F, Tuple2<W, A>>) : WriterTOf<F, W, A>, WriterTKindedJ<F, W, A> {

    companion object {

        inline fun <reified F, reified W, A> pure(a: A, MM: Monoid<W> = monoid(), AF: Applicative<F> = applicative()) =
                WriterT(AF.pure(Tuple2(MM.empty(), a)))

        inline fun <reified F, W, A> both(w: W, a: A, MF: Monad<F> = monad()) = WriterT(MF.pure(Tuple2(w, a)))

        inline fun <reified F, W, A> fromTuple(z: Tuple2<W, A>, MF: Monad<F> = monad()) = WriterT(MF.pure(z))

        operator fun <F, W, A> invoke(value: Kind<F, Tuple2<W, A>>): WriterT<F, W, A> = WriterT(value)

        inline fun <reified F, W, A> putT(vf: Kind<F, A>, w: W, FF: Functor<F> = functor()): WriterT<F, W, A> =
                WriterT(FF.map(vf, { v -> Tuple2(w, v) }))

        inline fun <reified F, W, A> put(a: A, w: W, applicativeF: Applicative<F> = applicative()): WriterT<F, W, A> =
                putT(applicativeF.pure(a), w)

        fun <F, W, A> putT2(vf: Kind<F, A>, w: W, FF: Functor<F>): WriterT<F, W, A> =
                WriterT(FF.map(vf, { v -> Tuple2(w, v) }))

        fun <F, W, A> put2(a: A, w: W, AF: Applicative<F>): WriterT<F, W, A> =
                putT2(AF.pure(a), w, AF)

        inline fun <reified F, W> tell(l: W): WriterT<F, W, Unit> = put(Unit, l)

        fun <F, W> tell2(l: W, AF: Applicative<F>): WriterT<F, W, Unit> = put2(Unit, l, AF)

        inline fun <reified F, reified W, A> value(v: A, monoidW: Monoid<W> = monoid()):
                WriterT<F, W, A> = put(v, monoidW.empty())

        inline fun <reified F, reified W, A> valueT(vf: Kind<F, A>, monoidW: Monoid<W> = monoid()): WriterT<F, W, A> =
                putT(vf, monoidW.empty())

        inline fun <reified F, W, A> empty(MMF: MonoidK<F> = monoidK()): WriterTOf<F, W, A> = WriterT(MMF.empty())

        fun <F, W, A> pass(fa: Kind<WriterTPartialOf<F, W>, Tuple2<(W) -> W, A>>, MF: Monad<F>): WriterT<F, W, A> =
                WriterT(MF.flatMap(fa.reify().content(MF), { tuple2FA -> MF.map(fa.reify().write(MF), { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) }) }))

        fun <F, W, A, B> tailRecM(a: A, f: (A) -> Kind<WriterTPartialOf<F, W>, Either<A, B>>, MF: Monad<F>): WriterT<F, W, B> =
                WriterT(MF.tailRecM(a, {
                    MF.map(f(it).reify().value) {
                        val value = it.b
                        when (value) {
                            is Either.Left<A, B> -> Either.Left(value.a)
                            is Either.Right<A, B> -> Either.Right(it.a toT value.b)
                        }
                    }
                }))
    }

    fun tell(w: W, SG: Semigroup<W>, MF: Monad<F>): WriterT<F, W, A> = mapAcc ({ SG.combine(it, w) }, MF)

    fun listen(MF: Monad<F>): Kind<WriterTPartialOf<F, W>, Tuple2<W, A>> =
            WriterT(MF.flatMap(content(MF), { a -> MF.map(write(MF), { l -> Tuple2(l, Tuple2(l, a)) }) }))

    fun content(FF: Functor<F>): Kind<F, A> = FF.map(value, { it.b })

    fun write(FF: Functor<F>): Kind<F, W> = FF.map(value, { it.a })

    fun reset(MM: Monoid<W>, MF: Monad<F>): WriterT<F, W, A> = mapAcc ({ MM.empty() }, MF)

    fun <B> map(f: (A) -> B, FF: Functor<F>): WriterT<F, W, B> = WriterT(FF.map(value, { it.a toT f(it.b) }))

    inline fun <U> mapAcc(crossinline f: (W) -> U, MF: Monad<F>): WriterT<F, U, A> = transform ({ f(it.a) toT it.b }, MF)

    inline fun <C, U> bimap(crossinline g: (W) -> U, crossinline f: (A) -> C, MF: Monad<F>): WriterT<F, U, C> = transform ({ g(it.a) toT f(it.b) }, MF)

    fun swap(MF: Monad<F>): WriterT<F, A, W> = transform({ it.b toT it.a }, MF)

    fun <B> ap(ff: WriterTOf<F, W, (A) -> B>, SG: Semigroup<W>, MF: Monad<F>): WriterT<F, W, B> =
            ff.reify().flatMap({ map(it, MF) }, SG, MF)

    inline fun <B> flatMap(crossinline f: (A) -> WriterT<F, W, B>, SG: Semigroup<W>, MF: Monad<F>): WriterT<F, W, B> =
            WriterT(MF.flatMap(value, { value -> MF.map(f(value.b).value, { SG.combine(it.a, value.a) toT it.b }) }))

    inline fun <B, U> transform(crossinline f: (Tuple2<W, A>) -> Tuple2<U, B>, MF: Monad<F>): WriterT<F, U, B> = WriterT(MF.flatMap(value, { MF.pure(f(it)) }))

    fun <B> liftF(fa: Kind<F, B>, AF: Applicative<F>): WriterT<F, W, B> = WriterT(AF.map2(fa, value, { it.b.a toT it.a }))

    inline fun <C> semiflatMap(crossinline f: (A) -> Kind<F, C>, SG: Semigroup<W>, MF: Monad<F>): WriterT<F, W, C> = flatMap({ liftF(f(it), MF) }, SG, MF)

    inline fun <B> subflatMap(crossinline f: (A) -> Tuple2<W, B>, MF: Monad<F>): WriterT<F, W, B> = transform({ f(it.b) }, MF)

    fun combineK(y: WriterTOf<F, W, A>, SF: SemigroupK<F>): WriterT<F, W, A> =
            WriterT(SF.combineK(value, y.reify().value))
}
