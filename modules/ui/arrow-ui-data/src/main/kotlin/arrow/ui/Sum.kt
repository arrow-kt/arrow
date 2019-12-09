package arrow.ui

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@higherkind
data class Sum<F, G, V>(
  val left: Kind<F, V>,
  val right: Kind<G, V>,
  val side: Side = Side.Left
) : SumOf<F, G, V>, SumKindedJ<F, G, V> {

  sealed class Side {
    object Left : Side()
    object Right : Side()
  }

  fun <A> coflatmap(CF: Comonad<F>, CG: Comonad<G>, f: (Sum<F, G, V>) -> A): Sum<F, G, A> = Sum(
    CF.run { left.coflatMap { f(Sum(left, right, Side.Left)) } },
    CG.run { right.coflatMap { f(Sum(left, right, Side.Right)) } },
    side
  )

  fun <A> map(FF: Functor<F>, FG: Functor<G>, f: (V) -> A): Sum<F, G, A> = Sum(
    FF.run { left.map(f) },
    FG.run { right.map(f) },
    side
  )

  fun extract(CF: Comonad<F>, CG: Comonad<G>): V = CF.run {
    CG.run {
      when (side) {
        is Side.Left -> left.extract()
        is Side.Right -> right.extract()
      }
    }
  }

  fun changeSide(side: Side): Sum<F, G, V> = Sum(left, right, side)

  companion object {
    fun <F, G, V> left(ls: Kind<F, V>, rs: Kind<G, V>): Sum<F, G, V> = Sum(ls, rs, Side.Left)

    fun <F, G, V> right(ls: Kind<F, V>, rs: Kind<G, V>): Sum<F, G, V> = Sum(ls, rs, Side.Right)
  }
}
