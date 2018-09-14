package arrow.data

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Comonad

@higherkind
interface Day<F, G, A> : DayOf<F, G, A> {

  fun <X, Y, R> runDay(ff: (left: Kind<F, X>, right: Kind<G, Y>, get: (X, Y) -> A) -> R): R

  fun <B> coflatMap(f: (DayOf<F, G, A>) -> B): Day<F, G, B> = TODO()

  fun <B> map(f: (A) -> B): Day<F, G, B> = object : Day<F, G, B> {
    override fun <X, Y, R> runDay(ff: (left: Kind<F, X>, right: Kind<G, Y>, get: (X, Y) -> B) -> R): R =
      this@Day.runDay { left, right, get: (X, Y) -> A ->
        ff(left, right) { x, y ->
          f(get(x, y))
        }
      }
  }

  fun extract(CF: Comonad<F>, CG: Comonad<G>): A =
    runDay<Any, Any, A> { left, right, get -> get(CF.run { left.extract() }, CG.run { right.extract() }) }

  //  fun <B> ap(CF: Comonad<F>, CG: Comonad<G>, ff: DayOf<F, G, X, Y, (A) -> B>): Day<F, G, X, Y, B> =
  //    Day(left, right) { x, y -> ff.fix().extract(CF, CG).invoke(get(x, y)) }

  companion object {
    @Suppress("UNCHECKED_CAST")
    fun <F, G, A> just(AF: Applicative<F>, AG: Applicative<G>, a: A): Day<F, G, A> =
      object : Day<F, G, A> {
        override fun <X, Y, R> runDay(ff: (left: Kind<F, X>, right: Kind<G, Y>, get: (X, Y) -> A) -> R): R =
          ff(AF.just(Any() as X), AG.just(Any() as Y)) { _, _ -> a }
      }
  }
}
