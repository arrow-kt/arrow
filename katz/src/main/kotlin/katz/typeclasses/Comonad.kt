package katz

/**
 * The dual of monads, used to extract values from F
 */
interface Comonad<F> : Functor<F>, Typeclass {

    fun <A, B> coflatMap(fa: HK<F, A>, f: (HK<F, A>) -> B): HK<F, B>

    fun <A> extract(fa: HK<F, A>): A

}

inline fun <reified F> comonad(): Comonad<F> =
        instance(InstanceParametrizedType(Comonad::class.java, listOf(F::class.java)))
