package arrow.mtl

import arrow.Kind
import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.identity
import arrow.core.toT
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid

typealias AccumTFun<S, F, A> = (S) -> Kind<F, Tuple2<S, A>>

typealias AccumTFunOf<S, F, A> = Kind<F, AccumTFun<S, F, A>>

fun <S, F, A> AccumTOf<S, F, A>.runM(MF: Monad<F>, initial: S): Kind<F, Tuple2<S, A>> = runAccumT(MF, fix().accumT, initial)

@higherkind
data class AccumT<S, F, A>(val accumT: AccumTFunOf<S, F, A>) : AccumTOf<S, F, A> {

  companion object {

    fun <S, F, A> just(MW: Monoid<S>, MM: Monad<F>, a: A): AccumT<S, F, A> =
      AccumT(MM.just { w: S -> MM.just(MW.empty() toT a) })

    operator fun <S, F, A> invoke(AF: Applicative<F>, accumTFun: AccumTFun<S, F, A>) =
      AF.run {
        AccumT(just(accumTFun))
      }

    fun <S, F, A, B> tailRecM(
      MF: Monad<F>,
      a: A,
      fa: (A) -> Kind<AccumTPartialOf<S, F>, Either<A, B>>
    ) = MF.run {
      AccumT(MF) { s: S ->
        tailRecM(Tuple2(s, a)) { (s1, a0) ->
          fa(a0).fix().runAccumT(MF, s1).map { (s2, ab) ->
            ab.bimap({ a1 -> s2 toT a1 }, { b -> s2 toT b })
          }
        }
      }
    }

    fun <F, S, A> liftF(AF: Applicative<F>, fa: Kind<F, A>): AccumT<S, F, A> = AF.run {
      AccumT(AF) { s -> fa.map { a -> Tuple2(s, a) } }
    }

    fun <S, F> look(MS: Monoid<S>, MF: Monad<F>): AccumT<S, F, S> =
      looks(MS, MF, ::identity)

    fun <S, F, A> looks(MS: Monoid<S>, MF: Monad<F>, fs: (S) -> A): AccumT<S, F, A> =
      AccumT(MF.just { s: S -> MF.just(MS.empty() toT fs(s)) })

    fun <S, F> add(MF: Monad<F>, s: S): AccumT<S, F, Unit> =
      AccumT(MF.just { _: S -> MF.just(s toT Unit) })
  }

  fun runAccumT(MF: Monad<F>, s: S): Kind<F, Tuple2<S, A>> =
    runAccumT(MF, accumT, s)

  fun execAccumT(MF: Monad<F>, s: S): Kind<F, S> =
    MF.run {
      runAccumT(MF, s).map {
        it.a
      }
    }

  fun evalAccumT(MF: Monad<F>, s: S): Kind<F, A> =
    MF.run {
      runAccumT(MF, s).map {
        it.b
      }
    }

  fun <G, B> mapAccumT(
    MF: Monad<F>,
    AG: Applicative<G>,
    fas: (Kind<F, Tuple2<S, A>>) -> Kind<G, Tuple2<S, B>>
  ): AccumT<S, G, B> =
    AccumT(AG) { s: S ->
      fas(runAccumT(MF, s))
    }

  fun <B> map(MF: Functor<F>, fab: (A) -> B): AccumT<S, F, B> =
    MF.run {
      AccumT(
        accumT.map { fa ->
          { s: S ->
            fa(s).map { it.a toT fab(it.b) }
          }
        }
      )
    }

  fun <B> flatMap(MS: Monoid<S>, MF: Monad<F>, fa: (A) -> AccumTOf<S, F, B>): AccumT<S, F, B> =
    AccumT(MF) { s: S ->
      MF.run {
        runAccumT(MF, s).flatMap { (s1, a) ->
          fa(a).fix().runAccumT(MF, MS.run { s.combine(s1) }).flatMap { (s2, b) ->
            MF.just(MS.run { s1.combine(s2) } toT b)
          }
        }
      }
    }

  fun <B> ap(MS: Monoid<S>, MF: Monad<F>, mf: AccumTOf<S, F, (A) -> B>): AccumT<S, F, B> =
    (this to mf.fix()).let { (mv, mf) ->
      AccumT(MF) { s: S ->
        MF.run {
          runAccumT(MF, mv.accumT, s).flatMap { (s1, v) ->
            runAccumT(MF, mf.accumT, MS.run { s.combine(s1) }).flatMap { (s2, f) ->
              MF.just(MS.run { s1.combine(s2) } toT f(v))
            }
          }
        }
      }
    }

  fun toStateT(MF: Monad<F>, MS: Monoid<S>): StateT<F, S, A> =
    StateT(MF) { s: S ->
      MF.run {
        runAccumT(MF, s).map { (s1, a) ->
          MS.run { s.combine(s1) } toT a
        }
      }
    }
}

fun <F, S, A> runAccumT(MF: Monad<F>, accumT: AccumTFunOf<S, F, A>, s: S): Kind<F, Tuple2<S, A>> =
  MF.run {
    accumT.flatMap {
      it(s)
    }
  }

fun <F, S, A> ReaderT<F, S, A>.toAccumT(
  AF: Applicative<F>,
  FF: Functor<F>,
  MS: Monoid<S>
): AccumT<S, F, A> =
  this.run.let { f ->
    AccumT(AF) { s: S ->
      FF.run {
        f(s).map { a ->
          MS.empty() toT a
        }
      }
    }
  }

fun <F, S, A> WriterT<F, S, A>.toAccumT(FF: Functor<F>): AccumT<S, F, A> =
  value().let { fsa ->
    FF.run {
      AccumT(
        fsa.map {
          { _: S ->
            fsa
          }
        })
    }
  }
