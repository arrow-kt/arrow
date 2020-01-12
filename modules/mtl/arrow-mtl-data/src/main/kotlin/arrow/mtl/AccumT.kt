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

@higherkind
data class AccumT<W, M, A>(val accumT: AccumTFunOf<W, M, A>) : AccumTOf<W, M, A> {

  companion object {

    fun <W, M, A> just(MM: Monad<M>, a: A): AccumT<W, M, A> =
      AccumT(MM.just { w: W -> MM.just(a toT w) })

    fun <W, M, A, B> tailRecM(MM: Monad<M>, a: A, fa: (A) -> Kind<AccumTPartialOf<W, M>, Either<A, B>>): AccumT<W, M, B> = MM.run {
      val accumTFun = { w: W ->
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

  fun runAccumT(MM: Monad<M>, w: W): Kind<M, Tuple2<A, W>> =
    runAccumT(MM, accumT, w)

  fun execAccumT(MM: Monad<M>, w: W): Kind<M, W> =
    MM.run {
      runAccumT(MM, w).map {
        it.b
      }
    }

  fun evalAccumT(MM: Monad<M>, w: W): Kind<M, A> =
    MM.run {
      runAccumT(MM, w).map {
        it.a
      }
    }

  fun <N, B> mapAccumT(MM: Monad<M>, AN: Applicative<N>, f: (Kind<M, Tuple2<A, W>>
  ) -> Kind<N, Tuple2<B, W>>): AccumT<W, N, B> {

    val fn = { w: W ->
      val mResult = runAccumT(MM, w)
      f(mResult)
    }

    val ap = AN.just(fn)

    return AccumT(ap)
  }

  fun look(MW: Monoid<W>, MM: Monad<M>): AccumT<W, M, W> =
    AccumT(MM.just { w: W -> MM.just(w toT MW.empty()) })

  fun looks(MW: Monoid<W>, MM: Monad<M>, f: (W) -> A): AccumT<W, M, A> =
    AccumT(MM.just { w: W -> MM.just(f(w) toT MW.empty()) })

  fun add(MM: Monad<M>, w: W): AccumT<W, M, Unit> =
    AccumT(MM.just { _: W -> MM.just(Unit toT w) })

  fun <B> map(MF: Functor<M>, fab: (A) -> B): AccumT<W, M, B> =
    MF.run {
      AccumT(
        accumT.map { fa ->
          { w: W ->
            fa(w).map { fab(it.a) toT it.b }
          }
        }
      )
    }

  fun <B> flatMap(MW: Monoid<W>, MM: Monad<M>, fa: (A) -> AccumTOf<W, M, B>): AccumT<W, M, B> {
    val accumTFunc = { w: W ->
      MM.run {
        runAccumT(MM, w).flatMap { (a, w1) ->

          val combinedW = MW.run { w.combine(w1) }

          fa(a).fix().runAccumT(MM, combinedW).flatMap { (b, w2) ->
            MM.just(b toT MW.run { w1.combine(w2) })
          }
        }
      }
    }

    return AccumT(MM.just(accumTFunc))
  }

  fun <B> ap(MW: Monoid<W>, MM: Monad<M>, ff: AccumTOf<W, M, (A) -> B>): AccumT<W, M, B> =
    flatMap(MW, MM) { a ->
      ff.fix().map(MM) { f ->
        f(a)
      }
    }
}

fun <M, W, A> runAccumT(MM: Monad<M>, accumT: AccumTFunOf<W, M, A>, w: W): Kind<M, Tuple2<A, W>> =
  MM.run {
    accumT.flatMap {
      it(w)
    }
  }
