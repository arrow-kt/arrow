package kategory

private typealias AnyFunc = (Any?) -> Any?

@higherkind data class Coyoneda<F, P, A>(val pivot: HK<F, P>, internal val ks: List<AnyFunc>) : CoyonedaKind<F, P, A> {

    @Suppress("UNCHECKED_CAST")
    private val transform: (P) -> A = {
        var curr: Any? = it
        ks.forEach { curr = it(curr) }
        curr as A
    }

    fun lower(FF: Functor<F>): HK<F, A> = FF.map(pivot, transform)

    @Suppress("UNCHECKED_CAST")
    fun <B> map(f: (A) -> B): Coyoneda<F, P, B> = Coyoneda(pivot, ks + f as AnyFunc)

    fun toYoneda(FF: Functor<F>): Yoneda<F, A> =
            object : Yoneda<F, A>() {
                override fun <B> apply(f: (A) -> B): HK<F, B> = map(f).lower(FF)
            }

    companion object {
        @Suppress("UNCHECKED_CAST")
        inline fun <reified U, A, B> apply(fa: HK<U, A>, noinline f: (A) -> B): Coyoneda<U, A, B> = unsafeApply(fa, listOf(f as AnyFunc))

        inline fun <reified U, A, B> unsafeApply(fa: HK<U, A>, f: List<AnyFunc>): Coyoneda<U, A, B> = Coyoneda(fa, f)

        fun <U, P> functor(): CoyonedaFunctorInstance<U, P> = object : CoyonedaFunctorInstance<U, P> {}

    }

}
