package kategory

/**
 * Inject type class as described in "Data types a la carte" (Swierstra 2008).
 *
 * @see [[http://www.staff.science.uu.nl/~swier004/publications/2008-jfp.pdf]]
 */
interface Inject<in F, out G> : Typeclass {

    fun inj(): FunctionK<F, G>

    fun <A> invoke(fa: HK<F, A>): HK<G, A> = inj()(fa)

}

inline fun <reified F, reified G> inject(): Inject<F, G> =
        instance(InstanceParametrizedType(Inject::class.java, listOf(F::class.java, G::class.java)))
