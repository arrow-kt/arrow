package arrow.syntax.monoid

import arrow.*

inline fun <reified A> A.empty(FT: Monoid<A> = monoid()): A = FT.empty()

inline fun <reified A> Collection<A>.combineAll(FT: Monoid<A> = monoid()): A = FT.combineAll(this)