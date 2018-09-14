package arrow.data

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Comonad

@higherkind
interface Day<F, G, A> : DayOf<F, G, A> {

  fun <X, Y, R> runDay(ff: (left: Kind<F, X>, right: Kind<G, Y>, get: (X, Y) -> A) -> R): R

  fun extract(CF: Comonad<F>, CG: Comonad<G>): A =
    runDay<Any, Any, A> { left, right, get -> get(CF.run { left.extract() }, CG.run { right.extract() }) }

  fun <B> map(f: (A) -> B): Day<F, G, B> = object : Day<F, G, B> {
    override fun <X, Y, R> runDay(ff: (left: Kind<F, X>, right: Kind<G, Y>, get: (X, Y) -> B) -> R): R =
      this@Day.runDay { left, right, get: (X, Y) -> A ->
        ff(left, right) { x, y ->
          f(get(x, y))
        }
      }
  }

  fun <B> ap(CF: Comonad<F>, CG: Comonad<G>, f: DayOf<F, G, (A) -> B>): Day<F, G, B> = object : Day<F, G, B> {
    override fun <X, Y, R> runDay(ff: (left: Kind<F, X>, right: Kind<G, Y>, get: (X, Y) -> B) -> R): R =
        this@Day.runDay { left, right, get: (X, Y) -> A ->
          ff(left, right) { x, y -> f.fix().extract(CF, CG)(get(x, y)) }
        }
  }

  @Suppress("UNCHECKED_CAST")
  fun <B> coflatMap(AF: Applicative<F>, AG: Applicative<G>, f: (DayOf<F, G, A>) -> B): Day<F, G, B> = object : Day<F, G, B> {
    override fun <X, Y, R> runDay(ff: (left: Kind<F, X>, right: Kind<G, Y>, get: (X, Y) -> B) -> R): R =
        this@Day.runDay { left, right, get: (X, Y) -> A ->
          ff(left, right) { x, y -> f(Day(AF, AG, x as Any, y as Any, get as (Any, Any) -> A)) }
        }
  }

  companion object {
    @Suppress("UNCHECKED_CAST")
    fun <F, G, A> just(AF: Applicative<F>, AG: Applicative<G>, a: A): Day<F, G, A> =
      object : Day<F, G, A> {
        override fun <X, Y, R> runDay(ff: (left: Kind<F, X>, right: Kind<G, Y>, get: (X, Y) -> A) -> R): R =
          ff(AF.just(Any() as X), AG.just(Any() as Y)) { _, _ -> a }
      }

    @Suppress("UNCHECKED_CAST")
    operator fun <F, G, A> invoke(AF: Applicative<F>, AG: Applicative<G>, left: Any, right: Any, f: (Any, Any) -> A): Day<F, G, A> = object : Day<F, G, A> {
      override fun <X, Y, R> runDay(ff: (left: Kind<F, X>, right: Kind<G, Y>, get: (X, Y) -> A) -> R): R =
          ff(AF.just(left as X), AG.just(right as Y), f as (X, Y) -> A)
    }
  }
}
