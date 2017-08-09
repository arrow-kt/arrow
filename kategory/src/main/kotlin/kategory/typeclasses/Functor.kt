package kategory

interface Functor<F> : Typeclass {

    fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B>

    fun <A, B> lift(f: (A) -> B): (HK<F, A>) -> HK<F, B> =
            { fa: HK<F, A> ->
                map(fa, f)
            }
}

inline fun <reified F> functor(): Functor<F> = instance(InstanceParametrizedType(Functor::class.java, listOf(F::class.java)))

//Syntax

inline fun <reified F, A, B> HK<F, A>.map(FT : Functor<F> = functor(), noinline f: (A) -> B): HK<F, B> = FT.map(this, f)

inline fun <reified F, A, B> ((A) -> B).lift(FT : Functor<F> = functor()): (HK<F, A>) -> HK<F, B> = FT.lift(this)