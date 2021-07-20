package arrow.core

import arrow.typeclasses.Semigroup

public data class Const<A, out T>(private val value: A) {

  @Suppress("UNCHECKED_CAST")
  public fun <U> retag(): Const<A, U> =
    this as Const<A, U>

  public companion object {

    @Deprecated(
      "This constructor is duplicated with Const. Use Const instead.",
      ReplaceWith("Const(a)", "arrow.core.Const")
    )
    public fun <A, T> just(a: A): Const<A, T> =
      Const(a)
  }

  public fun value(): A =
    value

  public fun <U> map(f: (T) -> U): Const<A, U> =
    retag()

  public inline fun <B, C> zip(
    SG: Semigroup<A>,
    b: Const<A, B>,
    map: (T, B) -> C
  ): Const<A, C> =
    retag<C>().combine(SG, b.retag())

  public inline fun <B, C, D> zip(
    SG: Semigroup<A>,
    b: Const<A, B>,
    c: Const<A, C>,
    map: (T, B, C) -> D
  ): Const<A, D> =
    retag<D>()
      .combine(SG, b.retag())
      .combine(SG, c.retag())

  public inline fun <B, C, D, E> zip(
    SG: Semigroup<A>,
    b: Const<A, B>,
    c: Const<A, C>,
    d: Const<A, D>,
    map: (T, B, C, D) -> E
  ): Const<A, E> =
    retag<E>()
      .combine(SG, b.retag())
      .combine(SG, c.retag())
      .combine(SG, d.retag())

  public inline fun <B, C, D, E, F> zip(
    SG: Semigroup<A>,
    b: Const<A, B>,
    c: Const<A, C>,
    d: Const<A, D>,
    e: Const<A, E>,
    map: (T, B, C, D, E) -> F
  ): Const<A, F> =
    retag<F>()
      .combine(SG, b.retag())
      .combine(SG, c.retag())
      .combine(SG, d.retag())
      .combine(SG, e.retag())

  public inline fun <B, C, D, E, F, G> zip(
    SG: Semigroup<A>,
    b: Const<A, B>,
    c: Const<A, C>,
    d: Const<A, D>,
    e: Const<A, E>,
    f: Const<A, F>,
    map: (A, B, C, D, E, F) -> G
  ): Const<A, G> =
    retag<G>()
      .combine(SG, b.retag())
      .combine(SG, c.retag())
      .combine(SG, d.retag())
      .combine(SG, e.retag())
      .combine(SG, f.retag())

  public inline fun <B, C, D, E, F, G, H> zip(
    SG: Semigroup<A>,
    b: Const<A, B>,
    c: Const<A, C>,
    d: Const<A, D>,
    e: Const<A, E>,
    f: Const<A, F>,
    g: Const<A, G>,
    map: (A, B, C, D, E, F, G) -> H
  ): Const<A, T> =
    retag<T>()
      .combine(SG, b.retag())
      .combine(SG, c.retag())
      .combine(SG, d.retag())
      .combine(SG, e.retag())
      .combine(SG, f.retag())
      .combine(SG, g.retag())

  public inline fun <B, C, D, E, F, G, H, I> zip(
    SG: Semigroup<A>,
    b: Const<A, B>,
    c: Const<A, C>,
    d: Const<A, D>,
    e: Const<A, E>,
    f: Const<A, F>,
    g: Const<A, G>,
    h: Const<A, H>,
    map: (A, B, C, D, E, F, G, H) -> I
  ): Const<A, T> =
    retag<T>()
      .combine(SG, b.retag())
      .combine(SG, c.retag())
      .combine(SG, d.retag())
      .combine(SG, e.retag())
      .combine(SG, f.retag())
      .combine(SG, g.retag())
      .combine(SG, h.retag())

  public inline fun <B, C, D, E, F, G, H, I, J> zip(
    SG: Semigroup<A>,
    b: Const<A, B>,
    c: Const<A, C>,
    d: Const<A, D>,
    e: Const<A, E>,
    f: Const<A, F>,
    g: Const<A, G>,
    h: Const<A, H>,
    i: Const<A, I>,
    map: (A, B, C, D, E, F, G, H, I) -> J
  ): Const<A, J> =
    retag<J>()
      .combine(SG, b.retag())
      .combine(SG, c.retag())
      .combine(SG, d.retag())
      .combine(SG, e.retag())
      .combine(SG, f.retag())
      .combine(SG, g.retag())
      .combine(SG, h.retag())
      .combine(SG, i.retag())

  public inline fun <B, C, D, E, F, G, H, I, J, K> zip(
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
    map: (A, B, C, D, E, F, G, H, I, J) -> K
  ): Const<A, K> =
    retag<K>()
      .combine(SG, b.retag())
      .combine(SG, c.retag())
      .combine(SG, d.retag())
      .combine(SG, e.retag())
      .combine(SG, f.retag())
      .combine(SG, g.retag())
      .combine(SG, h.retag())
      .combine(SG, i.retag())
      .combine(SG, j.retag())

  override fun toString(): String =
    "$Const($value)"
}

public fun <A, T> Const<A, T>.combine(SG: Semigroup<A>, that: Const<A, T>): Const<A, T> =
  Const(SG.run { this@combine.value().combine(that.value()) })

public inline fun <A> A.const(): Const<A, Nothing> =
  Const(this)

public fun <A, T, U> Const<A, T>.contramap(f: (U) -> T): Const<A, U> =
  retag()

public operator fun <A : Comparable<A>, T> Const<A, T>.compareTo(other: Const<A, T>): Int =
  value().compareTo(other.value())
