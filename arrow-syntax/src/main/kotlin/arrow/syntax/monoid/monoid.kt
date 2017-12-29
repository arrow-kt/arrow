package arrow.syntax.monoid

import arrow.typeclasses.Monoid
import arrow.typeclasses.monoid

inline fun <reified A> A.empty(FT: Monoid<A> = monoid()): A = FT.empty()

inline fun <reified A> Collection<A>.combineAll(FT: Monoid<A> = monoid()): A = FT.combineAll(this)