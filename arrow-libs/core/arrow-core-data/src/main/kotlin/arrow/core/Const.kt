package arrow.core

import arrow.Kind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
class ForConst private constructor() {
  companion object
}
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
typealias ConstOf<A, T> = arrow.Kind2<ForConst, A, T>
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
typealias ConstPartialOf<A> = arrow.Kind<ForConst, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
inline fun <A, T> ConstOf<A, T>.fix(): Const<A, T> =
  this as Const<A, T>

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
fun <A, T> ConstOf<A, T>.value(): A = this.fix().value()

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

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>): String =
    "$Const(${SA.run { value.show() }})"

  override fun toString(): String =
    "$Const($value)"
}

@Deprecated(
  "Kind is deprecated, and will be removed in 0.13.0. Please use the combine method defined for Const instead",
  level = DeprecationLevel.WARNING
)
fun <A, T> ConstOf<A, T>.combine(SG: Semigroup<A>, that: ConstOf<A, T>): Const<A, T> =
  Const(SG.run { value().combine(that.value()) })

fun <A, T> Const<A, T>.combine(SG: Semigroup<A>, that: Const<A, T>): Const<A, T> =
  Const(SG.run { value().combine(that.value()) })

@Deprecated(
  "Kind is deprecated, and will be removed in 0.13.0. Please use the ap method defined for Const instead",
  ReplaceWith(
    "Const.mapN(MA, this, arg1)",
    "arrow.core.Const"
  ),
  DeprecationLevel.WARNING
)
fun <A, T, U> ConstOf<A, T>.ap(SG: Semigroup<A>, ff: ConstOf<A, (T) -> U>): Const<A, U> =
  fix().retag<U>().combine(SG, ff.fix().retag())

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
fun <T, A, G> ConstOf<A, Kind<G, T>>.sequence(GA: Applicative<G>): Kind<G, Const<A, T>> =
  fix().traverse(GA, ::identity)

inline fun <A> A.const(): Const<A, Nothing> =
  Const(this)

fun <A, T, U> Const<A, T>.contramap(f: (U) -> T): Const<A, U> =
  retag()

fun <A, T> Const<A, T>.eqv(EQ: Eq<A>, b: Const<A, T>): Boolean =
  EQ.run {
    value().eqv(b.value())
  }

fun <A, T> Eq.Companion.const(EQ: Eq<A>): Eq<Const<A, T>> = object : Eq<Const<A, T>> {
  override fun Const<A, T>.eqv(b: Const<A, T>): Boolean =
    eqv(EQ, b)
}

@Deprecated(
  "Hash is going to be removed, please use hashCode() instead",
  ReplaceWith("hashCode()"),
  level = DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.hashWithSalt(HA: Hash<A>, salt: Int): Int =
  HA.run {
    value().hashWithSalt(salt)
  }

@Deprecated(
  "Order is going to be removed, please use compareTo() instead",
  ReplaceWith("compareTo(b)"),
  level = DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.compare(ORD: Order<A>, b: Const<A, T>): Ordering =
  ORD.run {
    value().compare(b.value())
  }

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
