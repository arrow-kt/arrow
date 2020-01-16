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

typealias Listen<S, F, A> = (Kind<F, A>) -> Kind<F, Tuple2<A, S>>

typealias Pass<S, F, A> = (Kind<F, (S) -> S>) -> Kind<F, Tuple2<A, S>>

@higherkind
data class AccumT<S, F, A>(val accumT: AccumTFunOf<S, F, A>) : AccumTOf<S, F, A> {

  companion object {

    fun <S, F, A> just(MW: Monoid<S>, MM: Monad<F>, a: A): AccumT<S, F, A> =
      AccumT(MM.just { w: S -> MM.just(a toT MW.empty()) })

    operator fun <S, F, A> invoke(AF: Applicative<F>, run: AccumTFun<S, F, A>) =
      AF.run {
        AccumT(just(run))
      }

    fun <S, F, A, B> tailRecM(MM: Monad<F>, a: A, fa: (A) -> Kind<AccumTPartialOf<S, F>, Either<A, B>>): AccumT<S, F, B> = MM.run {
      val accumTFun = { w: S ->
        tailRecM(Tuple2(a, w)) { (a0, w0) ->
          fa(a0).fix().runAccumT(MM, w0).map { (ab, w1) ->
            ab.bimap({ a1 -> a1 toT w1 }, { b -> b toT w1 })
          }
        }
      }

      AccumT(
        MM.just(accumTFun)
      )
    }

    fun <S, F> look(MW: Monoid<S>, MM: Monad<F>): AccumT<S, F, S> =
      AccumT(MM.just { w: S -> MM.just(w toT MW.empty()) })

    fun <S, F, A> looks(MW: Monoid<S>, MM: Monad<F>, fs: (S) -> A): AccumT<S, F, A> =
      AccumT(MM.just { w: S -> MM.just(fs(w) toT MW.empty()) })

    fun <S, F> add(MM: Monad<F>, s: S): AccumT<S, F, Unit> =
      AccumT(MM.just { _: S -> MM.just(Unit toT s) })
  }

  fun runAccumT(MM: Monad<F>, w: S): Kind<F, Tuple2<A, S>> =
    runAccumT(MM, accumT, w)

  fun execAccumT(MM: Monad<F>, w: S): Kind<F, S> =
    MM.run {
      runAccumT(MM, w).map {
        it.b
      }
    }

  fun evalAccumT(MM: Monad<F>, w: S): Kind<F, A> =
    MM.run {
      runAccumT(MM, w).map {
        it.a
      }
    }

  fun <N, B> mapAccumT(MM: Monad<F>, AN: Applicative<N>, f: (Kind<F, Tuple2<A, S>>
  ) -> Kind<N, Tuple2<B, S>>): AccumT<S, N, B> {

    val fn = { w: S ->
      val mResult = runAccumT(MM, w)
      f(mResult)
    }

    val ap = AN.just(fn)

    return AccumT(ap)
  }


  fun <B> map(MF: Functor<F>, fab: (A) -> B): AccumT<S, F, B> =
    MF.run {
      AccumT(
        accumT.map { fa ->
          { w: S ->
            fa(w).map { fab(it.a) toT it.b }
          }
        }
      )
    }

  fun <B> flatMap(MS: Monoid<S>, MF: Monad<F>, fa: (A) -> AccumTOf<S, F, B>): AccumT<S, F, B> {
    val accumTFunc = { w: S ->
      MF.run {
        runAccumT(MF, w).flatMap { (a, w1) ->

          val combinedW = MS.run { w.combine(w1) }

          fa(a).fix().runAccumT(MF, combinedW).flatMap { (b, w2) ->
            MF.just(b toT MS.run { w1.combine(w2) })
          }
        }
      }
    }

    return AccumT(MF.just(accumTFunc))
  }

  fun <B> ap(MS: Monoid<S>, MF: Monad<F>, mf: AccumTOf<S, F, (A) -> B>): AccumT<S, F, B> =
    let { mv ->
      AccumT(MF.just { s: S ->
        MF.run {
          runAccumT(MF, mv.accumT, s).flatMap { (v, s1) ->
            runAccumT(MF, mf.fix().accumT, MS.run { s.combine(s1) }).flatMap { (f, s2) ->
              MF.just(f(v) toT MS.run { s1.combine(s2) })
            }
          }
        }
      })
    }
}

fun <F, S, A> runAccumT(MF: Monad<F>, accumT: AccumTFunOf<S, F, A>, w: S): Kind<F, Tuple2<A, S>> =
  MF.run {
    accumT.flatMap {
      it(w)
    }
  }

fun <F, S, A> ReaderT<F, S, A>.toAccumT(AF: Applicative<F>, FF: Functor<F>, MS: Monoid<S>): AccumT<S, F, A> =
  this.run.let { f ->
    val func = { w: S ->
      FF.run {
        f(w).map { a ->
          a toT MS.empty()
        }
      }
    }

    AccumT(AF.just(func))
  }


fun <F, W, A> WriterT<F, W, A>.toAccumT(FF: Functor<F>): AccumT<W, F, A> {
  val accumTFunc = this.value().let { m ->
    FF.run {
      m.map {
        { _: W ->
          m.map { it.reverse() }
        }
      }
    }
  }

  return AccumT(accumTFunc)
}

