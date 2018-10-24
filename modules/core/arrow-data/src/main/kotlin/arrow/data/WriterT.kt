package arrow.data

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import arrow.higherkind
import arrow.typeclasses.*

@Suppress("UNCHECKED_CAST")
fun <F, W, A> WriterTOf<F, W, A>.value(): Kind<F, Tuple2<W, A>> = this.fix().value

@higherkind
data class WriterT<F, W, A>(val value: Kind<F, Tuple2<W, A>>) : WriterTOf<F, W, A>, WriterTKindedJ<F, W, A> {

  companion object {

    fun <F, W, A> just(AF: Applicative<F>, MM: Monoid<W>, a: A) =
      WriterT(AF.just(Tuple2(MM.empty(), a)))

    fun <F, W, A> both(MF: Monad<F>, w: W, a: A) = WriterT(MF.just(Tuple2(w, a)))

    fun <F, W, A> fromTuple(MF: Monad<F>, z: Tuple2<W, A>) = WriterT(MF.just(z))

    operator fun <F, W, A> invoke(value: Kind<F, Tuple2<W, A>>): WriterT<F, W, A> = WriterT(value)

    fun <F, W, A> putT(FF: Functor<F>, vf: Kind<F, A>, w: W): WriterT<F, W, A> = FF.run {
      WriterT(vf.map { v -> Tuple2(w, v) })
    }

    fun <F, W, A> put(AF: Applicative<F>, a: A, w: W): WriterT<F, W, A> =
      putT(AF, AF.just(a), w)

    fun <F, W, A> putT2(FF: Functor<F>, vf: Kind<F, A>, w: W): WriterT<F, W, A> = FF.run {
      WriterT(vf.map { v -> Tuple2(w, v) })
    }

    fun <F, W, A> put2(AF: Applicative<F>, a: A, w: W): WriterT<F, W, A> =
      putT2(AF, AF.just(a), w)

    fun <F, W> tell(AF: Applicative<F>, l: W): WriterT<F, W, Unit> = put(AF, Unit, l)

    fun <F, W> tell2(AF: Applicative<F>, l: W): WriterT<F, W, Unit> = put2(AF, Unit, l)

    fun <F, W, A> value(AF: Applicative<F>, monoidW: Monoid<W>, v: A):
      WriterT<F, W, A> = put(AF, v, monoidW.empty())

    fun <F, W, A> valueT(AF: Applicative<F>, monoidW: Monoid<W>, vf: Kind<F, A>): WriterT<F, W, A> =
      putT(AF, vf, monoidW.empty())

    fun <F, W, A> empty(MMF: MonoidK<F>): WriterTOf<F, W, A> = WriterT(MMF.empty())

    fun <F, W, A> pass(MF: Monad<F>, fa: Kind<WriterTPartialOf<F, W>, Tuple2<(W) -> W, A>>): WriterT<F, W, A> = MF.run {
      WriterT(fa.fix().content(this).flatMap { tuple2FA -> fa.fix().write(this).map { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) } })
    }

    fun <F, W, A, B> tailRecM(MF: Monad<F>, a: A, f: (A) -> Kind<WriterTPartialOf<F, W>, Either<A, B>>): WriterT<F, W, B> =
      WriterT(MF.tailRecM(a) {
        val value = f(it).fix().value
        MF.run {
          value.map {
            val right = it.b
            when (right) {
              is Either.Left -> Either.Left(right.a)
              is Either.Right -> Either.Right(it.a toT right.b)
            }
          }
        }
      })
  }

  fun tell(MF: Monad<F>, SG: Semigroup<W>, w: W): WriterT<F, W, A> = mapAcc(MF) { SG.run { it.combine(w) } }

  fun listen(MF: Monad<F>): Kind<WriterTPartialOf<F, W>, Tuple2<W, A>> = MF.run {
    WriterT(content(this).flatMap { a -> write(this).map { l -> Tuple2(l, Tuple2(l, a)) } })
  }

  fun content(FF: Functor<F>): Kind<F, A> = FF.run {
    value.map { it.b }
  }

  fun write(FF: Functor<F>): Kind<F, W> = FF.run {
    value.map { it.a }
  }

  fun reset(MF: Monad<F>, MM: Monoid<W>): WriterT<F, W, A> = mapAcc(MF) { MM.empty() }

  fun <B> map(FF: Functor<F>, f: (A) -> B): WriterT<F, W, B> = FF.run {
    WriterT(value.map { it.a toT f(it.b) })
  }

  fun <U> mapAcc(MF: Monad<F>, f: (W) -> U): WriterT<F, U, A> = transform(MF) { f(it.a) toT it.b }

  fun <C, U> bimap(MF: Monad<F>, g: (W) -> U, f: (A) -> C): WriterT<F, U, C> = transform(MF) { g(it.a) toT f(it.b) }

  fun swap(MF: Monad<F>): WriterT<F, A, W> = transform(MF) { it.b toT it.a }

  fun <B> ap(MF: Monad<F>, SG: Semigroup<W>, ff: WriterTOf<F, W, (A) -> B>): WriterT<F, W, B> =
    ff.fix().flatMap(MF, SG) { map(MF, it) }

  fun <B> flatMap(MF: Monad<F>, SG: Semigroup<W>, f: (A) -> WriterT<F, W, B>): WriterT<F, W, B> = MF.run {
    WriterT(value.flatMap { value -> f(value.b).value.map { SG.run { it.a.combine(value.a) } toT it.b } })
  }

  fun <B, U> transform(MF: Monad<F>, f: (Tuple2<W, A>) -> Tuple2<U, B>): WriterT<F, U, B> = MF.run {
    WriterT(value.flatMap { just(f(it)) })
  }

  fun <B> liftF(AF: Applicative<F>, fa: Kind<F, B>): WriterT<F, W, B> =
    WriterT(AF.run { fa.map2(value) { it.b.a toT it.a } })

  fun <C> semiflatMap(MF: Monad<F>, SG: Semigroup<W>, f: (A) -> Kind<F, C>): WriterT<F, W, C> =
    flatMap(MF, SG) { liftF(MF, f(it)) }

  fun <B> subflatMap(MF: Monad<F>, f: (A) -> Tuple2<W, B>): WriterT<F, W, B> = transform(MF) { f(it.b) }

  fun combineK(SF: SemigroupK<F>, y: WriterTOf<F, W, A>): WriterT<F, W, A> = SF.run {
    WriterT(value.combineK(y.fix().value))
  }
}
