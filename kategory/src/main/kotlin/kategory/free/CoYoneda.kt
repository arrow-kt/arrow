package kategory

typealias CoYonedaKind<U, P, A> = HK3<CoYoneda.F, U, P, A>

typealias CoYonedaF<U, P> = HK2<CoYoneda.F, U, P>

fun <U, A, B> CoYonedaKind<U, A, B>.ev(): CoYoneda<U, A, B> = this as CoYoneda<U, A, B>

private typealias AnyFunc = (Any?) -> Any?

data class CoYoneda<F, P, A>(val pivot: HK<F, P>, internal val ks: List<AnyFunc>) : CoYonedaKind<F, P, A> {
    class F private constructor()

    private val transform: (P) -> A = {
        var curr: Any? = it
        ks.forEach { curr = it(curr) }
        curr as A
    }

    fun lower(FF: Functor<F>): HK<F, A> = FF.map(pivot, transform)

    @Suppress("UNCHECKED_CAST")
    fun <B> map(f: (A) -> B): CoYoneda<F, P, B> = CoYoneda(pivot, ks + f as AnyFunc)

    fun toYoneda(FF: Functor<F>): Yoneda<F, A> =
            object : Yoneda<F, A> {
                override fun <B> apply(f: (A) -> B): HK<F, B> = map(f).lower(FF)
            }

    companion object {
        @Suppress("UNCHECKED_CAST")
        inline fun <reified U, A, B> apply(fa: HK<U, A>, noinline f: (A) -> B): CoYoneda<U, A, B> = unsafeApply(fa, listOf(f as AnyFunc))

        inline fun <reified U, A, B> unsafeApply(fa: HK<U, A>, f: List<AnyFunc>): CoYoneda<U, A, B> = CoYoneda(fa, f)

        fun <U, P> functor(): Functor<CoYonedaF<U, P>> = object : CoYonedaInstances<U, P> {}

    }

}
