package arrow.data

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import arrow.higherkind
import arrow.typeclasses.*

@Suppress("UNCHECKED_CAST")
inline fun <F, W, A> WriterTOf<F, W, A>.value(): Kind<F, Tuple2<W, A>> = this.fix().value

@higherkind
data class WriterT<F, W, A>(val value: Kind<F, Tuple2<W, A>>) : WriterTOf<F, W, A>, WriterTKindedJ<F, W, A> {

    companion object {

        fun <F, W, A> pure(MM: Monoid<W>, AF: Applicative<F>, a: A) =
                WriterT(AF.pure(Tuple2(MM.empty(), a)))

        fun <F, W, A> both(MF: Monad<F>, w: W, a: A) = WriterT(MF.pure(Tuple2(w, a)))

        fun <F, W, A> fromTuple(MF: Monad<F>, z: Tuple2<W, A>) = WriterT(MF.pure(z))

        operator fun <F, W, A> invoke(value: Kind<F, Tuple2<W, A>>): WriterT<F, W, A> = WriterT(value)

        fun <F, W, A> putT(FF: Functor<F>, vf: Kind<F, A>, w: W): WriterT<F, W, A> = FF.run {
            WriterT(vf.map({ v -> Tuple2(w, v) }))
        }

        fun <F, W, A> put(AF: Applicative<F>, a: A, w: W): WriterT<F, W, A> =
                putT(AF, AF.pure(a), w)

        fun <F, W, A> putT2(FF: Functor<F>, vf: Kind<F, A>, w: W): WriterT<F, W, A> = FF.run {
            WriterT(vf.map({ v -> Tuple2(w, v) }))
        }

        fun <F, W, A> put2(AF: Applicative<F>, a: A, w: W): WriterT<F, W, A> =
                putT2(AF, AF.pure(a), w)

        fun <F, W> tell(AF: Applicative<F>, l: W): WriterT<F, W, Unit> = put(AF, Unit, l)

        fun <F, W> tell2(AF: Applicative<F>, l: W): WriterT<F, W, Unit> = put2(AF, Unit, l)

        fun <F, W, A> value(AF: Applicative<F>, monoidW: Monoid<W>, v: A):
                WriterT<F, W, A> = put(AF, v, monoidW.empty())

        fun <F, W, A> valueT(AF: Applicative<F>, monoidW: Monoid<W>, vf: Kind<F, A>): WriterT<F, W, A> =
                putT(AF, vf, monoidW.empty())

        fun <F, W, A> empty(MMF: MonoidK<F>): WriterTOf<F, W, A> = WriterT(MMF.empty())

        fun <F, W, A> pass(fa: Kind<WriterTPartialOf<F, W>, Tuple2<(W) -> W, A>>, MF: Monad<F>): WriterT<F, W, A> = MF.run {
            WriterT(fa.fix().content(this).flatMap({ tuple2FA -> fa.fix().write(this).map({ l -> Tuple2(tuple2FA.a(l), tuple2FA.b) }) }))
        }

        fun <F, W, A, B> tailRecM(a: A, f: (A) -> Kind<WriterTPartialOf<F, W>, Either<A, B>>, MF: Monad<F>): WriterT<F, W, B> = MF.run {
            WriterT(tailRecM(a, {
                f(it).fix().value.map() {
                    val value = it.b
                    when (value) {
                        is Either.Left<A, B> -> Either.Left(value.a)
                        is Either.Right<A, B> -> Either.Right(it.a toT value.b)
                    }
                }
            }))
        }
    }

    fun tell(w: W, SG: Semigroup<W>, MF: Monad<F>): WriterT<F, W, A> = mapAcc(MF, { SG.run { it.combine(w) } })

    fun listen(MF: Monad<F>): Kind<WriterTPartialOf<F, W>, Tuple2<W, A>> = MF.run {
        WriterT(content(this).flatMap({ a -> write(this).map({ l -> Tuple2(l, Tuple2(l, a)) }) }))
    }

    fun content(FF: Functor<F>): Kind<F, A> = FF.run {
        value.map({ it.b })
    }

    fun write(FF: Functor<F>): Kind<F, W> = FF.run {
        value.map({ it.a })
    }

    fun reset(MF: Monad<F>, MM: Monoid<W>): WriterT<F, W, A> = mapAcc(MF, { MM.empty() })

    fun <B> map(FF: Functor<F>, f: (A) -> B): WriterT<F, W, B> = FF.run {
        WriterT(value.map({ it.a toT f(it.b) }))
    }

    inline fun <U> mapAcc(MF: Monad<F>, crossinline f: (W) -> U): WriterT<F, U, A> = transform({ f(it.a) toT it.b }, MF)

    inline fun <C, U> bimap(MF: Monad<F>, crossinline g: (W) -> U, crossinline f: (A) -> C): WriterT<F, U, C> = transform({ g(it.a) toT f(it.b) }, MF)

    fun swap(MF: Monad<F>): WriterT<F, A, W> = transform({ it.b toT it.a }, MF)

    fun <B> ap(MF: Monad<F>, SG: Semigroup<W>, ff: WriterTOf<F, W, (A) -> B>): WriterT<F, W, B> =
            ff.fix().flatMap(MF, SG, { map(MF, it) })

    inline fun <B> flatMap(MF: Monad<F>, SG: Semigroup<W>, crossinline f: (A) -> WriterT<F, W, B>): WriterT<F, W, B> = MF.run {
        WriterT(value.flatMap({ value -> f(value.b).value.map({ SG.run { it.a.combine(value.a) } toT it.b }) }))
    }

    inline fun <B, U> transform(crossinline f: (Tuple2<W, A>) -> Tuple2<U, B>, MF: Monad<F>): WriterT<F, U, B> = MF.run {
        WriterT(value.flatMap({ pure(f(it)) }))
    }

    fun <B> liftF(fa: Kind<F, B>, AF: Applicative<F>): WriterT<F, W, B> =
            WriterT(AF.run { fa.map2(value, { it.b.a toT it.a }) })

    inline fun <C> semiflatMap(crossinline f: (A) -> Kind<F, C>, SG: Semigroup<W>, MF: Monad<F>): WriterT<F, W, C> =
            flatMap(MF, SG, { liftF(f(it), MF) })

    inline fun <B> subflatMap(crossinline f: (A) -> Tuple2<W, B>, MF: Monad<F>): WriterT<F, W, B> = transform({ f(it.b) }, MF)

    fun combineK(y: WriterTOf<F, W, A>, SF: SemigroupK<F>): WriterT<F, W, A> = SF.run {
        WriterT(value.combineK(y.fix().value))
    }
}
