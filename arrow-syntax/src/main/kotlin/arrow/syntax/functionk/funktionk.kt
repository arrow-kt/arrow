package arrow.syntax.functionk

import arrow.*

fun <F, G, H> FunctionK<F, G>.or(h: FunctionK<H, G>): FunctionK<CoproductKindPartial<F, H>, G> =
        object : FunctionK<CoproductKindPartial<F, H>, G> {
            override fun <A> invoke(fa: CoproductKind<F, H, A>): HK<G, A> {
                return fa.ev().fold(this@or, h)
            }
        }