package arrow.core

import arrow.Kind
import arrow.KindDeprecation
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
) class ForConst private constructor() {
  companion object
}

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
) typealias ConstOf<A, T> = arrow.Kind2<ForConst, A, T>

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
) typealias ConstPartialOf<A> = arrow.Kind<ForConst, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)inline
fun <A, T> ConstOf<A, T>.fix(): Const<A, T> =
  this as Const<A, T>

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
) fun <A, T> ConstOf<A, T>.value(): A = this.fix().value()

data class Const<A, out T>(private val value: A) : ConstOf<A, T> {

  @Suppress("UNCHECKED_CAST")
  fun <U> retag(): Const<A, U> =
    this as Const<A, U>

  @Suppress("UNUSED_PARAMETER")
  @Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
  fun <G, U> traverse(GA: Applicative<G>, f: (T) -> Kind<G, U>): Kind<G, Const<A, U>> =
    GA.just(retag())

  @Suppress("UNUSED_PARAMETER")
  @Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
  fun <G, U> traverseFilter(GA: Applicative<G>, f: (T) -> Kind<G, Option<U>>): Kind<G, Const<A, U>> =
    GA.just(retag())

  companion object {
    fun <A, T> just(a: A): Const<A, T> =
      Const(a)
  }

  fun value(): A =
    value

  fun <U> map(f: (T) -> U): Const<A, U> =
    retag()

  inline fun <B, C> zip(
    SG: Semigroup<A>,
    b: Const<A, B>,
    map: (T, B) -> C
  ): Const<A, C> =
    retag<C>().combine(SG, b.retag())

  inline fun <B, C, D> zip(
    SG: Semigroup<A>,
    b: Const<A, B>,
    c: Const<A, C>,
    map: (T, B, C) -> D
  ): Const<A, D> =
    retag<D>()
      .combine(SG, b.retag())
      .combine(SG, c.retag())

  inline fun <B, C, D, E> zip(
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

  inline fun <B, C, D, E, F> zip(
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

  inline fun <B, C, D, E, F, G> zip(
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

  inline fun <B, C, D, E, F, G, H> zip(
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

  inline fun <B, C, D, E, F, G, H, I> zip(
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

  inline fun <B, C, D, E, F, G, H, I, J> zip(
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

  inline fun <B, C, D, E, F, G, H, I, J, K> zip(
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

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>): String =
    "$Const(${SA.run { value.show() }})"

  override fun toString(): String =
    "$Const($value)"

  fun combine(SG: Semigroup<A>, that: Const<A, @UnsafeVariance T>): Const<A, T> =
    Const(SG.run { value().combine(that.value()) })
}

@Deprecated(
  "Kind is deprecated, and will be removed in 0.13.0. Please use the combine method defined for Const instead",
  level = DeprecationLevel.WARNING
)
fun <A, T> ConstOf<A, T>.combine(SG: Semigroup<A>, that: ConstOf<A, T>): Const<A, T> =
  Const(SG.run { value().combine(that.value()) })

@Deprecated(
  "Kind is deprecated, and will be removed in 0.13.0. Please use the ap method defined for Const instead",
  ReplaceWith("fix().zip(SG, ff.fix()) { a, f -> f(a) }", "arrow.core.fix"),
  DeprecationLevel.WARNING
)
fun <A, T, U> ConstOf<A, T>.ap(SG: Semigroup<A>, ff: ConstOf<A, (T) -> U>): Const<A, U> =
  fix().zip<(T) -> U, U>(SG, ff.fix()) { a, f -> f(a) }

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
fun <T, A, G> ConstOf<A, Kind<G, T>>.sequence(GA: Applicative<G>): Kind<G, Const<A, T>> =
  fix().traverse(GA, ::identity)

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
