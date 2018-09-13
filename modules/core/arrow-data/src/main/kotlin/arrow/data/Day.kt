package arrow.data

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Comonad
import arrow.typeclasses.Functor

@higherkind
data class Day<F, G, X, Y, A>(val left: Kind<F, X>, val right: Kind<G, Y>, val get: (X, Y) -> A) {

  fun <B> extend(CF: Comonad<F>, CG: Comonad<G>, f: (Day<F, G, X, Y, A>) -> B): Day<F, G, X, Y, B> = Day(
      CF.run { left.coflatMap(f) },
      CG.run { right.coflatMap(f) },
      { left, right -> f(Day(left, right, get)) }
  )

  fun <B> map(f: (A) -> B): Day<F, G, X, Y, B> = Day(
      left,
      right,
      { x, y -> f(get(x, y)) }
  )

  fun extract(CF: Comonad<F>, CG: Comonad<G>): A =
      get(CF.run { left.extract() }, CG.run { right.extract() })

}