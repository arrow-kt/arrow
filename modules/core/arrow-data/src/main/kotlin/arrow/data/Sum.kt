package arrow.data

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@higherkind
data class Sum<F, G, V>(private val side: Side, val left: Kind<F, V>, val right: Kind<G, V>) : SumOf<F, G, V> {

  sealed class Side {
    object Left : Side()
    object Right : Side()
  }

  fun <A> extend(f: (Sum<F, G, V>) -> A, CF: Comonad<F>, CG: Comonad<G>): Sum<F, G, A> = CF.run {
    CG.run {
      Sum(
          side,
          left.coflatMap { f(Sum(Side.Left, left, right)) },
          right.coflatMap { f(Sum(Side.Right, left, right)) }
      )
    }
  }

  fun <A> map(f: (V) -> A, FF: Functor<F>, FG: Functor<G>): Sum<F, G, A> = FF.run {
    FG.run {
      Sum(side, left.map(f), right.map(f))
    }
  }

  fun extract(CF: Comonad<F>, CG: Comonad<G>): V = CF.run {
    CG.run {
      when (side) {
        is Side.Left -> left.extract()
        is Side.Right -> right.extract()
      }
    }
  }

}