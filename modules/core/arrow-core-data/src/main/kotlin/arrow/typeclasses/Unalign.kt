package arrow.typeclasses

import arrow.Kind
import arrow.core.Ior
import arrow.core.Tuple2

interface Unalign<F> : Semialign<F> {
  fun <A, B> unalign(ior: Kind<F, Ior<A, B>>): Tuple2<Kind<F, A>, Kind<F, B>>

  fun <A, B, C> unalignWith(fa: (C) -> Ior<A, B>, c: Kind<F, C>): Tuple2<Kind<F, A>, Kind<F, B>> =
    unalign(c.map(fa))
}
