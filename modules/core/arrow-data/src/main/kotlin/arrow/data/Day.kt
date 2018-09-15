package arrow.data

import arrow.Kind
import arrow.core.Tuple2
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Comonad

/*
 * This whole file is a world of gotchas.
 *
 * It's an abstract class with a hidden constructor to prevent people from using runDay.
 *
 * `runDay` is an encoding of Existential function, enabled by inputs directly piped to outputs.
 * The inputs and outputs are both `Any?` to avoid type annotations that won't be used.
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

  fun <B> ap(AF: Applicative<F>, AG: Applicative<G>, f: DayOf<F, G, (A) -> B>): Day<F, G, B> = object : Day<F, G, B>() {
    override fun <R> runDay(ff: (Kind<F, *>, Kind<G, *>, (Any?, Any?) -> B) -> R): R =
      this@Day.runDay { left, right, get ->
        f.fix().runDay { lf, rf, getf ->
          val l = AF.run { tupled(left, lf) }
          val r = AG.run { tupled(right, rf) }
          ff(l, r) { x, y ->
            // Kapachao? This is a safe cast because you can see l and r just above
            // Kotlin allows me to use x and y now that they're narrowed
            val _x = x as Tuple2<*, *>
            val _y = y as Tuple2<*, *>
            getf(x.b, y.b).invoke(get(x.a, y.a))
          }
        }
      }
  }

  fun <B> coflatMap(CF: Comonad<F>, CG: Comonad<G>, f: (DayOf<F, G, A>) -> B): Day<F, G, B> = object : Day<F, G, B>() {
    override fun <R> runDay(ff: (Kind<F, *>, Kind<G, *>, (Any?, Any?) -> B) -> R): R =
      this@Day.runDay { left, right, get ->
        val l = CF.run { left.duplicate() }
        val r = CG.run { right.duplicate() }
        ff(l, r) { x, y ->
          // Kapachao? This is a safe cast because you can see l and r just above
          // Kotlin allows me to use x and y now that they're narrowed
          val _x = x as Kind<F, *>
          val _y = y as Kind<G, *>
          f(Day(x, y, get))
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
        ff(left, right, f as (Any?, Any?) -> A)
    }
  }
}
