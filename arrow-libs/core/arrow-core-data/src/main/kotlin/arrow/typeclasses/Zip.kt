package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.Tuple2

@Deprecated(KindDeprecation)
/**
 * Zip is a typeclass that extends a Functor by providing a zip operation that takes the intersection of non-uniform shapes.
 */
interface Zip<F> : Semialign<F> {

  fun <A, B> Kind<F, A>.zip(other: Kind<F, B>): Kind<F, Tuple2<A, B>> =
    zipWith(other) { a: A, b: B -> Tuple2(a, b) }

  fun <A, B, C> Kind<F, A>.zipWith(other: Kind<F, B>, f: (A, B) -> C): Kind<F, C> =
    zip(other).map { f(it.a, it.b) }
}
