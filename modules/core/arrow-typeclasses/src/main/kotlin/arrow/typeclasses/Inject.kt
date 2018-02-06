package arrow.typeclasses

import arrow.*
import arrow.core.FunctionK

/**
 * Inject type class as described in "Data types a la carte" (Swierstra 2008).
 *
 * @see [[http://www.staff.science.uu.nl/~swier004/publications/2008-jfp.pdf]]
 */
@typeclass
interface Inject<F, G> : TC {

    fun inj(): FunctionK<F, G>

    fun <A> invoke(fa: HK<F, A>): HK<G, A> = inj()(fa)

}