package kategory

@higherkind abstract class Yoneda<F, A> : YonedaKind<F, A> {

    abstract fun <B> apply(f: (A) -> B): HK<F, B>

    fun lower(): HK<F, A> = apply { a -> a }

    fun <B> map(ff: (A) -> B, FF: Functor<F>): Yoneda<F, B> =
            object : Yoneda<F, B>() {
                override fun <C> apply(f: (B) -> C): HK<F, C> = this@Yoneda.apply({ f(ff(it)) })
            }

    fun toCoyoneda(): Coyoneda<F, A, A> = Coyoneda(lower(), listOf({ a: Any? -> a }))

    companion object {
        inline fun <reified U, A> apply(fa: HK<U, A>, FF: Functor<U> = functor()): Yoneda<U, A> =
                object : Yoneda<U, A>() {
                    override fun <B> apply(f: (A) -> B): HK<U, B> = FF.map(fa, f)
                }

        fun <U> functor(FU: Functor<U>): YonedaFunctorInstance<U> =
                YonedaFunctorInstanceImplicits.instance(FU)
    }
}