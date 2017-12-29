package arrow.syntax.semigroup

import arrow.typeclasses.Semigroup
import arrow.typeclasses.semigroup

inline fun <reified A> A.combine(b: A, FT: Semigroup<A> = semigroup()): A = FT.combine(this, b)