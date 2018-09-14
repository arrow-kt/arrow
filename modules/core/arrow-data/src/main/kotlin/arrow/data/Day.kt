package arrow.data

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Comonad

@higherkind
data class Day<F, G, X, Y, A>(val left: Kind<F, X>, val right: Kind<G, Y>, val get: (X, Y) -> A) : DayOf<F, G, X, Y, A> {

  fun <B> coflatMap(f: (DayOf<F, G, X, Y, A>) -> B): Day<F, G, X, Y, B> =
    Day(left, right) { _, _ -> f(this) }

  fun <B> map(f: (A) -> B): Day<F, G, X, Y, B> =
    Day(left, right) { x, y -> f(get(x, y)) }

  fun extract(CF: Comonad<F>, CG: Comonad<G>): A =
    get(CF.run { left.extract() }, CG.run { right.extract() })

  fun <B> ap(CF: Comonad<F>, CG: Comonad<G>, ff: DayOf<F, G, X, Y, (A) -> B>): Day<F, G, X, Y, B> =
    Day(left, right) { x, y -> ff.fix().extract(CF, CG).invoke(get(x, y)) }

  companion object {
    fun <F, G, A> just(AF: Applicative<F>, AG: Applicative<G>, a: A): Day<F, G, Unit, Unit, A> =
      Day(AF.just(Unit), AG.just(Unit)) { _, _ -> a }
  }
}
