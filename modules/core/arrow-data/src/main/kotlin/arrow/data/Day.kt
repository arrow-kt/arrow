package arrow.data

import arrow.Kind
import arrow.core.Tuple2
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Comonad

/*
 * This whole file is a world of gotchas.
 *
 * It's an abstract class with a hidden constructor to prevent people from using `runDay` directly.
 *
 * `runDay` is an encoding of an Existential function for <R>, meaning that it can't be passed as a constructor parameter.
 * The inputs and outputs are both `Any?` to avoid type annotations that won't be used externally.
 *
 * The other functions start by creating their own private `Day` object so they don't eagerly invoke `runDay`.
 *
 * There are only two constructors, and thank got they're type-safe externally.
 */

@higherkind
abstract class Day<F, G, A> private constructor() : DayOf<F, G, A> {

  internal abstract fun <R> runDay(ff: (Kind<F, *>, Kind<G, *>, (Any?, Any?) -> A) -> R): R

  fun extract(CF: Comonad<F>, CG: Comonad<G>): A =
    runDay { left, right, get -> get(CF.run { left.extract() }, CG.run { right.extract() }) }

  fun <B> map(f: (A) -> B): Day<F, G, B> = object : Day<F, G, B>() {
    override fun <R> runDay(ff: (left: Kind<F, *>, right: Kind<G, *>, get: (Any?, Any?) -> B) -> R): R =
      this@Day.runDay { left, right, get ->
        ff(left, right) { x, y ->
          f(get(x, y))
        }
      }
  }

  @Suppress("UNCHECKED_CAST")
  fun <B> ap(AF: Applicative<F>, AG: Applicative<G>, f: DayOf<F, G, (A) -> B>): Day<F, G, B> = object : Day<F, G, B>() {
    override fun <R> runDay(ff: (Kind<F, *>, Kind<G, *>, (Any?, Any?) -> B) -> R): R =
      this@Day.runDay { left, right, get ->
        f.fix().runDay { lf, rf, getf ->
          val l = AF.run { tupled(left, lf) }
          val r = AG.run { tupled(right, rf) }
          ff(l, r) { x, y ->
            // Kapachao? This is a safe cast because you can see l and r just above
            val xx = x as Tuple2<*, *>
            val yy = y as Tuple2<*, *>
            getf(xx.b, yy.b).invoke(get(xx.a, yy.a))
          }
        }
      }
  }

  @Suppress("UNCHECKED_CAST")
  fun <B> coflatMap(CF: Comonad<F>, CG: Comonad<G>, f: (DayOf<F, G, A>) -> B): Day<F, G, B> = object : Day<F, G, B>() {
    override fun <R> runDay(ff: (Kind<F, *>, Kind<G, *>, (Any?, Any?) -> B) -> R): R =
      this@Day.runDay { left, right, get ->
        val l = CF.run { left.duplicate() }
        val r = CG.run { right.duplicate() }
        ff(l, r) { x, y ->
          // Kapachao? This is a safe cast because you can see l and r just above
          val xx = x as Kind<F, *>
          val yy = y as Kind<G, *>
          f(Day(xx, yy, get))
        }
      }
  }

  companion object {
    fun <F, G, A> just(AF: Applicative<F>, AG: Applicative<G>, a: A): Day<F, G, A> =
      object : Day<F, G, A>() {
        override fun <R> runDay(ff: (Kind<F, *>, Kind<G, *>, (Any?, Any?) -> A) -> R): R =
          ff(AF.just(Unit), AG.just(Unit)) { _, _ -> a }
      }

    @Suppress("UNCHECKED_CAST")
    operator fun <F, G, X, Y, A> invoke(left: Kind<F, X>, right: Kind<G, Y>, f: (X, Y) -> A): Day<F, G, A> = object : Day<F, G, A>() {
      override fun <R> runDay(ff: (Kind<F, *>, Kind<G, *>, (Any?, Any?) -> A) -> R): R =
      // The cast is implementation-safe, as it'll only ever be used with the inputs passed
        ff(left, right, f as (Any?, Any?) -> A)
    }
  }
}
