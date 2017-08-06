package kategory

typealias CoYonedaKind<U, P, A> = HK3<CoYoneda.F, U, P, A>

typealias CoYonedaF<U, P> = HK2<CoYoneda.F, U, P>

fun <U, A, B> CoYonedaKind<U, A, B>.ev(): CoYoneda<U, A, B> =
        this as CoYoneda<U, A, B>

data class CoYoneda<FU, P, A>(val pivot: HK<FU, P>, internal val ks: List<(Any?) -> Any?>) : CoYonedaKind<FU, P, A> {
    class F private constructor()

    private val transform: (P) -> A = {
        var curr: Any? = it
        ks.forEach { curr = it(curr) }
        curr as A
    }

    fun lower(FF: Functor<FU>): HK<FU, A> =
            FF.map(pivot, transform)

    @Suppress("UNCHECKED_CAST")
    fun <B> map(f: (A) -> B): CoYoneda<FU, P, B> =
            CoYoneda(pivot, ks + f as (Any?) -> Any)

    fun toYoneda(FF: Functor<FU>): Yoneda<FU, A> =
            object : Yoneda<FU, A> {
                override fun <B> apply(f: (A) -> B): HK<FU, B> = map(f).lower(FF)
            }

    companion object {
        @Suppress("UNCHECKED_CAST")
        inline fun <reified U, A, B> apply(fa: HK<U, A>, noinline f: (A) -> B): CoYoneda<U, A, B> =
                usafeApply(fa, listOf(f as (Any?) -> Any))

        inline fun <reified U, A, B> usafeApply(fa: HK<U, A>, f: List<(Any?) -> Any?>): CoYoneda<U, A, B> =
                CoYoneda(fa, f)

        fun <U, P> functor(): Functor<CoYonedaF<U, P>> = object : CoYonedaInstances<U, P> {}

    }

}
