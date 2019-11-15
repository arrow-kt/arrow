package arrow.typeclasses

import arrow.Kind
import arrow.core.Ior
import arrow.core.identity

interface Semialign<F> : Functor<F> {
  fun <A, B> align(left: Kind<F, A>, right: Kind<F, B>): Kind<F, Ior<A, B>> = alignWith(::identity, left, right)

  fun <A, B, C> alignWith(fa: (Ior<A, B>) -> C, a: Kind<F, A>, b: Kind<F, B>): Kind<F, C> = align(a, b).map(fa)
}
