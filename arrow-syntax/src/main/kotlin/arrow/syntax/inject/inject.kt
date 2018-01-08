package arrow.syntax.inject

import arrow.*
import arrow.typeclasses.Inject
import arrow.typeclasses.inject

inline fun <reified F, reified G, A> HK<F, A>.inj(FT: Inject<F, G> = inject()) : HK<G, A> = FT.invoke(this)