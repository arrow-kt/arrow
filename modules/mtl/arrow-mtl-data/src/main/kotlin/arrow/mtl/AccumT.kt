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

typealias AccumTFun<W, M, A> = (W) -> Kind<M, Tuple2<A, W>>

typealias AccumTFunOf<W, M, A> = Kind<M, AccumTFun<W, M, A>>

typealias Listen<W, M, A> = (Kind<M, A>) -> Kind<M, Tuple2<A, W>>

typealias Pass<W, M, A> = (Kind<M, (W) -> W>) -> Kind<M, Tuple2<A, W>>

@higherkind
data class AccumT<S, F, A>(val accumT: AccumTFunOf<S, F, A>) : AccumTOf<S, F, A> {

  companion object {

    fun <S, F, A> just(MW: Monoid<S>, MM: Monad<F>, a: A): AccumT<S, F, A> =
      AccumT(MM.just { w: S -> MM.just(a toT MW.empty()) })

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

  fun look(MW: Monoid<S>, MM: Monad<F>): AccumT<S, F, S> =
    AccumT(MM.just { w: S -> MM.just(w toT MW.empty()) })

  fun looks(MW: Monoid<S>, MM: Monad<F>, f: (S) -> A): AccumT<S, F, A> =
    AccumT(MM.just { w: S -> MM.just(f(w) toT MW.empty()) })

  fun add(MM: Monad<F>, w: S): AccumT<S, F, Unit> =
    AccumT(MM.just { _: S -> MM.just(Unit toT w) })

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
      AccumT(MF.just { w: S ->
        MF.run {
          runAccumT(MF, mf.fix().accumT, w).flatMap { (f, w1) ->
            runAccumT(MF, mv.accumT, MS.run { w.combine(w1) }).flatMap { (v, w2) ->
              MF.just(f(v) toT MS.run { w1.combine(w2) })
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
  val accumTFunc = this.value().let {
    m ->
    FF.run {
      m.map {
        { _: W -> m.map { it.reverse() }
        }
      }
    }
  }

  return AccumT(accumTFunc)
}

