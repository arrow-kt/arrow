package arrow.mtl

import arrow.Kind
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
  }

  fun runAccumT(MM: Monad<M>, w: W): Kind<M, Tuple2<A, W>> =
    MM.run {
      accumT.flatMap {
        it(w)
      }
    }

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

  fun <B> flatMap(MM: Monad<M>, fab: (A) -> AccumTOf<W, M, B>): AccumT<W, M, B> =
    MM.run {
      AccumT(accumT.flatMap { orig ->
        MM.just { w: W ->
          orig(w).flatMap {
            fab(it.a).fix().runAccumT(MM, it.b)
          }
        }
      })
    }
}
