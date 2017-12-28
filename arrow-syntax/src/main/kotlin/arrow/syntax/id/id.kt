package arrow.syntax.id

import arrow.*

fun <A, G, B> Id<A>.traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Id<B>> = GA.map(f(this.ev().value), { Id(it) })