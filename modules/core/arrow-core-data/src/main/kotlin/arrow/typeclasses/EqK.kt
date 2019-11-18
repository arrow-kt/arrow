package arrow.typeclasses

import arrow.Kind

interface EqK<F> {
  fun <A> Kind<F, A>.eqK(other: Kind<F, A>, EQ: Eq<A>): Boolean
}
