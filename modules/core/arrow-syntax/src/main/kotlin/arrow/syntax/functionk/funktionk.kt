package arrow.syntax.functionk

import arrow.*
import arrow.core.*
import arrow.data.*

fun <F, G, H> FunctionK<F, G>.or(h: FunctionK<H, G>): FunctionK<CoproductPartialOf<F, H>, G> =
        object : FunctionK<CoproductPartialOf<F, H>, G> {
            override fun <A> invoke(fa: CoproductOf<F, H, A>): Kind<G, A> {
                return fa.fix().fold(this@or, h)
            }
        }