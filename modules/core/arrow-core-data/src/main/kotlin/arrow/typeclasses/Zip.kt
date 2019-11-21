package arrow.typeclasses

import arrow.Kind
import arrow.core.Tuple2

/**
 * Zip is a Functor supporting a zip operation that takes the intersection of non-uniform shapes.
 */
interface Zip<F> : Semialign<F> {
  /**
   * Combines to structures by taking the intersection of their shapes
   * and using Tuple2 to hold the elements.
   */
  fun <A, B> Kind<F, A>.zip(other: Kind<F, B>): Kind<F, Tuple2<A, B>> =
    zipWith({ a: A, b: B -> Tuple2(a, b) }, other)

  /**
   * Combines to structures by taking the intersection of their shapes
   * and combining the elements with the given function.
   */
  fun <A, B, C> Kind<F, A>.zipWith(f: (A, B) -> C, other: Kind<F, B>): Kind<F, C> =
    zip(other).map { f(it.a, it.b) }
}
