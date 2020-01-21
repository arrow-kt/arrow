package arrow.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidK
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK

fun <W, F, A> WriterTOf<W, F, A>.value(): Kind<F, Tuple2<W, A>> = this.fix().value()

@higherkind
data class WriterT<W, F, A>(private val value: Kind<F, Tuple2<W, A>>) : WriterTOf<W, F, A>, WriterTKindedJ<W, F, A> {

  companion object {

    fun <W, F, A> just(AF: Applicative<F>, MM: Monoid<W>, a: A) =
      WriterT(AF.just(Tuple2(MM.empty(), a)))

    fun <W, F, A> both(AF: Applicative<F>, w: W, a: A) =
      WriterT(AF.just(Tuple2(w, a)))

    fun <W, F, A> fromTuple(AF: Applicative<F>, z: Tuple2<W, A>) =
      WriterT(AF.just(z))

    operator fun <W, F, A> invoke(value: Kind<F, Tuple2<W, A>>): WriterT<W, F, A> =
      WriterT(value)

    fun <W, F, A> putT(FF: Functor<F>, vf: Kind<F, A>, w: W): WriterT<W, F, A> = FF.run {
      WriterT(vf.map { v -> Tuple2(w, v) })
    }

    fun <W, F, A> put(AF: Applicative<F>, a: A, w: W): WriterT<W, F, A> =
      putT(AF, AF.just(a), w)

    fun <W, F, A> putT2(FF: Functor<F>, vf: Kind<F, A>, w: W): WriterT<W, F, A> = FF.run {
      WriterT(vf.map { v -> Tuple2(w, v) })
    }

    fun <W, F, A> put2(AF: Applicative<F>, a: A, w: W): WriterT<W, F, A> =
      putT2(AF, AF.just(a), w)

    fun <W, F> tell(AF: Applicative<F>, l: W): WriterT<W, F, Unit> =
      put(AF, Unit, l)

    fun <W, F> tell2(AF: Applicative<F>, l: W): WriterT<W, F, Unit> =
      put2(AF, Unit, l)

    fun <W, F, A> value(AF: Applicative<F>, monoidW: Monoid<W>, v: A): WriterT<W, F, A> =
      put(AF, v, monoidW.empty())

    fun <W, F, A> valueT(AF: Applicative<F>, monoidW: Monoid<W>, vf: Kind<F, A>): WriterT<W, F, A> =
      putT(AF, vf, monoidW.empty())

    fun <W, F, A> empty(MMF: MonoidK<F>): WriterTOf<W, F, A> =
      WriterT(MMF.empty())

    fun <W, F, A> pass(MF: Monad<F>, fa: WriterTOf<W, F, Tuple2<(W) -> W, A>>): WriterT<W, F, A> = MF.run {
      WriterT(fa.fix().content(this).flatMap { tuple2FA -> fa.fix().write(this).map { l -> Tuple2(tuple2FA.a(l), tuple2FA.b) } })
    }

    fun <W, F, A, B> tailRecM(MF: Monad<F>, a: A, f: (A) -> WriterTOf<W, F, Either<A, B>>): WriterT<W, F, B> =
      WriterT(MF.tailRecM(a) {
        val value = f(it).value()
        MF.run {
          value.map { (a, right) ->
            when (right) {
              is Either.Left -> Either.Left(right.a)
              is Either.Right -> Either.Right(a toT right.b)
            }
          }
        }
      })

    fun <W, F, A> liftF(fa: Kind<F, A>, MM: Monoid<W>, AF: Applicative<F>): WriterT<W, F, A> = AF.run {
      WriterT(fa.map { a -> Tuple2(MM.empty(), a) })
    }
  }

  fun value(): Kind<F, Tuple2<W, A>> = value

  fun tell(MF: Monad<F>, SG: Semigroup<W>, w: W): WriterT<W, F, A> =
    mapAcc(MF) { SG.run { it.combine(w) } }

  fun listen(MF: Monad<F>): WriterTOf<W, F, Tuple2<W, A>> = MF.run {
    WriterT(content(this).flatMap { a -> write(this).map { l -> Tuple2(l, Tuple2(l, a)) } })
  }

  fun content(FF: Functor<F>): Kind<F, A> = FF.run {
    value.map { it.b }
  }

  fun write(FF: Functor<F>): Kind<F, W> = FF.run {
    value.map { it.a }
  }

  fun reset(MF: Monad<F>, MM: Monoid<W>): WriterT<W, F, A> =
    mapAcc(MF) { MM.empty() }

  fun <B> map(FF: Functor<F>, f: (A) -> B): WriterT<W, F, B> = FF.run {
    WriterT(value.map { it.a toT f(it.b) })
  }

  fun <U> mapAcc(MF: Monad<F>, f: (W) -> U): WriterT<U, F, A> =
    transform(MF) { f(it.a) toT it.b }

  fun <C, U> bimap(MF: Monad<F>, g: (W) -> U, f: (A) -> C): WriterT<U, F, C> =
    transform(MF) { g(it.a) toT f(it.b) }

  fun swap(MF: Monad<F>): WriterT<A, F, W> =
    transform(MF) { it.b toT it.a }

  fun <B> ap(AF: Applicative<F>, SG: Semigroup<W>, ff: WriterTOf<W, F, (A) -> B>): WriterT<W, F, B> =
    WriterT(AF.map(value, ff.value()) { (a, b) ->
      Tuple2(SG.run { a.a.combine(b.a) }, b.b(a.b))
    })

  fun <B> flatMap(MF: Monad<F>, SG: Semigroup<W>, f: (A) -> WriterTOf<W, F, B>): WriterT<W, F, B> = MF.run {
    WriterT(value.flatMap { value -> f(value.b).value().map { SG.run { value.a.combine(it.a) } toT it.b } })
  }

  fun <B, U> transform(MF: Monad<F>, f: (Tuple2<W, A>) -> Tuple2<U, B>): WriterT<U, F, B> = MF.run {
    WriterT(value.flatMap { just(f(it)) })
  }

  fun <B> liftF(AF: Applicative<F>, fa: Kind<F, B>): WriterT<W, F, B> =
    WriterT(AF.run { fa.map2(value) { it.b.a toT it.a } })

  fun <C> semiflatMap(MF: Monad<F>, SG: Semigroup<W>, f: (A) -> Kind<F, C>): WriterT<W, F, C> =
    flatMap(MF, SG) { liftF(MF, f(it)) }

  fun <B> subflatMap(MF: Monad<F>, f: (A) -> Tuple2<W, B>): WriterT<W, F, B> =
    transform(MF) { f(it.b) }

  fun combineK(SF: SemigroupK<F>, y: WriterTOf<W, F, A>): WriterT<W, F, A> = SF.run {
    WriterT(value.combineK(y.value()))
  }
}
