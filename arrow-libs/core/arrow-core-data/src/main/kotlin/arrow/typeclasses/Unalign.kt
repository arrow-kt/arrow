package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.Ior
import arrow.core.Tuple2

@Deprecated(KindDeprecation)
/**
 * Unalign extends Semialign thereby supporting an inverse function to align: It splits a union shape
 * into a tuple representing the component parts.
 */
interface Unalign<F> : Semialign<F> {

  fun <A, B> unalign(ior: Kind<F, Ior<A, B>>): Tuple2<Kind<F, A>, Kind<F, B>>

  fun <A, B, C> unalignWith(c: Kind<F, C>, fa: (C) -> Ior<A, B>): Tuple2<Kind<F, A>, Kind<F, B>> =
    unalign(c.map(fa))
}
