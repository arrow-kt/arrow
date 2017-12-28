package arrow.syntax.semigroup

import arrow.*

inline fun <reified A> A.combine(b: A, FT: Semigroup<A> = semigroup()): A = FT.combine(this, b)