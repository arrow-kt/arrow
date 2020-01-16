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

typealias AccumTFun<S, F, A> = (S) -> Kind<F, Tuple2<A, S>>

typealias AccumTFunOf<S, F, A> = Kind<F, AccumTFun<S, F, A>>

@higherkind
data class AccumT<S, F, A>(val accumT: AccumTFunOf<S, F, A>) : AccumTOf<S, F, A> {

  companion object {

    fun <S, F, A> just(MW: Monoid<S>, MM: Monad<F>, a: A): AccumT<S, F, A> =
      AccumT(MM.just { w: S -> MM.just(a toT MW.empty()) })

    operator fun <S, F, A> invoke(AF: Applicative<F>, accumTFun: AccumTFun<S, F, A>) =
      AF.run {
        AccumT(just(accumTFun))
      }

    fun <S, F, A, B> tailRecM(
      MF: Monad<F>,
      a: A,
      fa: (A) -> Kind<AccumTPartialOf<S, F>, Either<A, B>>
    ) = MF.run {
      val accumTFun = { w: S ->
        tailRecM(Tuple2(a, w)) { (a0, w0) ->
          fa(a0).fix().runAccumT(MF, w0).map { (ab, w1) ->
            ab.bimap({ a1 -> a1 toT w1 }, { b -> b toT w1 })
          }
        }
      }

      AccumT(
        MF.just(accumTFun)
      )
    }

    fun <S, F> look(MS: Monoid<S>, MF: Monad<F>): AccumT<S, F, S> =
      AccumT(MF.just { w: S -> MF.just(w toT MS.empty()) })

    fun <S, F, A> looks(MS: Monoid<S>, MF: Monad<F>, fs: (S) -> A): AccumT<S, F, A> =
      AccumT(MF.just { w: S -> MF.just(fs(w) toT MS.empty()) })

    fun <S, F> add(MM: Monad<F>, s: S): AccumT<S, F, Unit> =
      AccumT(MM.just { _: S -> MM.just(Unit toT s) })
  }

  fun runAccumT(MM: Monad<F>, s: S): Kind<F, Tuple2<A, S>> =
    runAccumT(MM, accumT, s)

  fun execAccumT(MF: Monad<F>, s: S): Kind<F, S> =
    MF.run {
      runAccumT(MF, s).map {
        it.b
      }
    }

  fun evalAccumT(MF: Monad<F>, s: S): Kind<F, A> =
    MF.run {
      runAccumT(MF, s).map {
        it.a
      }
    }

  fun <G, B> mapAccumT(
    MF: Monad<F>,
    AN: Applicative<G>,
    f: (Kind<F, Tuple2<A, S>>) -> Kind<G, Tuple2<B, S>>
  ): AccumT<S, G, B> {

    val accumTFun = { s: S ->
      f(runAccumT(MF, s))
    }

    return AccumT(AN.just(accumTFun))
  }

  fun <B> map(MF: Functor<F>, fab: (A) -> B): AccumT<S, F, B> =
    MF.run {
      AccumT(
        accumT.map { fa ->
          { s: S ->
            fa(s).map { fab(it.a) toT it.b }
          }
        }
      )
    }

  fun <B> flatMap(MS: Monoid<S>, MF: Monad<F>, fa: (A) -> AccumTOf<S, F, B>): AccumT<S, F, B> {
    val accumTFun = { s: S ->
      MF.run {
        runAccumT(MF, s).flatMap { (a, s1) ->
          fa(a).fix().runAccumT(MF,  MS.run { s.combine(s1) }).flatMap { (b, s2) ->
            MF.just(b toT MS.run { s1.combine(s2) })
          }
        }
      }
    }

    return AccumT(MF.just(accumTFun))
  }

  fun <B> ap(MS: Monoid<S>, MF: Monad<F>, mf: AccumTOf<S, F, (A) -> B>): AccumT<S, F, B> =
    (this to mf.fix()).let { (mv, mf) ->
      AccumT(MF.just { s: S ->
        MF.run {
          runAccumT(MF, mv.accumT, s).flatMap { (v, s1) ->
            runAccumT(MF, mf.accumT, MS.run { s.combine(s1) }).flatMap { (f, s2) ->
              MF.just(f(v) toT MS.run { s1.combine(s2) })
            }
          }
        }
      })
    }

  fun toStateT(MF: Monad<F>, MS: Monoid<S>): StateT<F, S, A> =
    StateT(MF) { s: S ->
      MF.run {
        runAccumT(MF, s).map { (a, s1) ->
          MS.run { s.combine(s1) } toT a
        }
      }
    }
}

fun <F, S, A> runAccumT(MF: Monad<F>, accumT: AccumTFunOf<S, F, A>, s: S): Kind<F, Tuple2<A, S>> =
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
    val accumTFun = { s: S ->
      FF.run {
        f(s).map { a ->
          a toT MS.empty()
        }
      }
    }

    AccumT(AF.just(accumTFun))
  }

fun <F, S, A> WriterT<F, S, A>.toAccumT(FF: Functor<F>): AccumT<S, F, A> {
  val accumTFun = this.value().let { m ->
    FF.run {
      m.map {
        { _: S ->
          m.map { it.reverse() }
        }
      }
    }
  }

  return AccumT(accumTFun)
}
