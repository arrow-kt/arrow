package katz

interface Functor<F> : Typeclass {

    fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B>

    fun <A, B> lift(f: (A) -> B): (HK<F, A>) -> HK<F, B> =
            { fa: HK<F, A> ->
                map(fa, f)
            }
}

inline fun <reified F> functor(): Functor<F> =
        instance(InstanceParametrizedType(Functor::class.java, listOf(F::class.java)))
