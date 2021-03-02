package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

data class Const<A, out T>(private val value: A) {

  @Suppress("UNCHECKED_CAST")
  fun <U> retag(): Const<A, U> =
    this as Const<A, U>

  companion object {
    fun <A, T> just(a: A): Const<A, T> =
      Const(a)

    inline fun <A, B, C, T> mapN(
      SG: Semigroup<A>,
      b: Const<A, B>,
      c: Const<A, C>,
      map: (A, B) -> T
    ): Const<A, T> =
      b.retag<T>()
        .combine(SG, c.retag())

    inline fun <A, B, C, D, T> mapN(
      SG: Semigroup<A>,
      b: Const<A, B>,
      c: Const<A, C>,
      d: Const<A, D>,
      map: (A, B, C) -> T
    ): Const<A, T> =
      b.retag<T>()
        .combine(SG, c.retag())
        .combine(SG, d.retag())

    inline fun <A, B, C, D, E, T> mapN(
      SG: Semigroup<A>,
      b: Const<A, B>,
      c: Const<A, C>,
      d: Const<A, D>,
      e: Const<A, E>,
      map: (A, B, C, D) -> T
    ): Const<A, T> =
      b.retag<T>()
        .combine(SG, c.retag())
        .combine(SG, d.retag())
        .combine(SG, e.retag())

    inline fun <A, B, C, D, E, F, T> mapN(
      SG: Semigroup<A>,
      b: Const<A, B>,
      c: Const<A, C>,
      d: Const<A, D>,
      e: Const<A, E>,
      f: Const<A, F>,
      map: (A, B, C, D, E) -> T
    ): Const<A, T> =
      b.retag<T>()
        .combine(SG, c.retag())
        .combine(SG, d.retag())
        .combine(SG, e.retag())
        .combine(SG, f.retag())

    inline fun <A, B, C, D, E, F, G, T> mapN(
      SG: Semigroup<A>,
      b: Const<A, B>,
      c: Const<A, C>,
      d: Const<A, D>,
      e: Const<A, E>,
      f: Const<A, F>,
      g: Const<A, G>,
      map: (A, B, C, D, E, F) -> T
    ): Const<A, T> =
      b.retag<T>()
        .combine(SG, c.retag())
        .combine(SG, d.retag())
        .combine(SG, e.retag())
        .combine(SG, f.retag())
        .combine(SG, g.retag())

    inline fun <A, B, C, D, E, F, G, H, T> mapN(
      SG: Semigroup<A>,
      b: Const<A, B>,
      c: Const<A, C>,
      d: Const<A, D>,
      e: Const<A, E>,
      f: Const<A, F>,
      g: Const<A, G>,
      h: Const<A, H>,
      map: (A, B, C, D, E, F, G) -> T
    ): Const<A, T> =
      b.retag<T>()
        .combine(SG, c.retag())
        .combine(SG, d.retag())
        .combine(SG, e.retag())
        .combine(SG, f.retag())
        .combine(SG, g.retag())
        .combine(SG, h.retag())

    inline fun <A, B, C, D, E, F, G, H, I, T> mapN(
      SG: Semigroup<A>,
      b: Const<A, B>,
      c: Const<A, C>,
      d: Const<A, D>,
      e: Const<A, E>,
      f: Const<A, F>,
      g: Const<A, G>,
      h: Const<A, H>,
      i: Const<A, I>,
      map: (A, B, C, D, E, F, G, H) -> T
    ): Const<A, T> =
      b.retag<T>()
        .combine(SG, c.retag())
        .combine(SG, d.retag())
        .combine(SG, e.retag())
        .combine(SG, f.retag())
        .combine(SG, g.retag())
        .combine(SG, h.retag())
        .combine(SG, i.retag())

    inline fun <A, B, C, D, E, F, G, H, I, J, T> mapN(
      SG: Semigroup<A>,
      b: Const<A, B>,
      c: Const<A, C>,
      d: Const<A, D>,
      e: Const<A, E>,
      f: Const<A, F>,
      g: Const<A, G>,
      h: Const<A, H>,
      i: Const<A, I>,
      j: Const<A, J>,
      map: (A, B, C, D, E, F, G, H, I) -> T
    ): Const<A, T> =
      b.retag<T>()
        .combine(SG, c.retag())
        .combine(SG, d.retag())
        .combine(SG, e.retag())
        .combine(SG, f.retag())
        .combine(SG, g.retag())
        .combine(SG, h.retag())
        .combine(SG, i.retag())
        .combine(SG, j.retag())

    inline fun <A, B, C, D, E, F, G, H, I, J, K, T> mapN(
      SG: Semigroup<A>,
      b: Const<A, B>,
      c: Const<A, C>,
      d: Const<A, D>,
      e: Const<A, E>,
      f: Const<A, F>,
      g: Const<A, G>,
      h: Const<A, H>,
      i: Const<A, I>,
      j: Const<A, J>,
      k: Const<A, K>,
      map: (A, B, C, D, E, F, G, H, I, J) -> T
    ): Const<A, T> =
      b.retag<T>()
        .combine(SG, c.retag())
        .combine(SG, d.retag())
        .combine(SG, e.retag())
        .combine(SG, f.retag())
        .combine(SG, g.retag())
        .combine(SG, h.retag())
        .combine(SG, i.retag())
        .combine(SG, j.retag())
        .combine(SG, k.retag())
  }

  fun value(): A =
    value

  fun <U> map(f: (T) -> U): Const<A, U> =
    retag()

  override fun toString(): String =
    "$Const($value)"
}

fun <A, T> Const<A, T>.combine(SG: Semigroup<A>, that: Const<A, T>): Const<A, T> =
  Const(SG.run { this@combine.value().combine(that.value()) })

inline fun <A> A.const(): Const<A, Nothing> =
  Const(this)

fun <A, T, U> Const<A, T>.contramap(f: (U) -> T): Const<A, U> =
  retag()

operator fun <A : Comparable<A>, T> Const<A, T>.compareTo(other: Const<A, T>): Int =
  value().compareTo(other.value())

fun <A, T> Semigroup.Companion.const(SA: Semigroup<A>): Semigroup<Const<A, T>> =
  object : Semigroup<Const<A, T>> {
    override fun Const<A, T>.combine(b: Const<A, T>): Const<A, T> =
      this.combine(SA, b)
  }

fun <A, T> Monoid.Companion.const(MA: Monoid<A>): Monoid<Const<A, T>> =
  object : Monoid<Const<A, T>> {
    override fun empty(): Const<A, T> =
      Const(MA.empty())

    override fun Const<A, T>.combine(b: Const<A, T>): Const<A, T> =
      this.combine(MA, b)
  }
