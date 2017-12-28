package arrow.syntax.inject

import arrow.*

inline fun <reified F, reified G, A> HK<F, A>.inj(FT: Inject<F, G> = inject()) : HK<G, A> = FT.invoke(this)