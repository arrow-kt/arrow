package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.Tuple2
import arrow.documented

/**
 * The [Semigroupal] type class for a given type `F` can be seen as an abstraction over the [cartesian product](https://en.wikipedia.org/wiki/Cartesian_product).
 * It defines the function [product].
 *
 * The [product] function for a given type `F`, `A` and `B` combines a `Kind<F, A>` and a `Kind<F, B>` into a `Kind<F, Tuple2<A, B>>`.
 * This function guarantees compliance with the following laws:
 *
 * [Semigroupal]s are associative under the bijection `f = (a,(b,c)) -> ((a,b),c)` or `f = ((a,b),c) -> (a,(b,c))`.
 * Therefore, the following laws also apply:
 *
 * ```kotlin
 * f((a.product(b)).product(c)) == a.product(b.product(c))
 * ```
 *
 * ```kotlin
 * f(a.product(b.product(c))) == (a.product(b)).product(c)
 * ```
 *
 */
@documented
@Deprecated(KindDeprecation)
interface Semigroupal<F> {

  /**
   * Multiplicatively combine F<A> and F<B> into F<Tuple2<A, B>>
   */
  fun <A, B> Kind<F, A>.product(fb: Kind<F, B>): Kind<F, Tuple2<A, B>>

  /**
   * Add support for the * syntax
   */
  operator fun <A, B> Kind<F, A>.times(fb: Kind<F, B>): Kind<F, Tuple2<A, B>> =
    this.product(fb)
}
