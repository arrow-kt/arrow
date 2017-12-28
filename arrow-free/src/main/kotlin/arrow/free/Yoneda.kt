package arrow

@higherkind abstract class Yoneda<F, A> : YonedaKind<F, A>, YonedaKindedJ<F, A> {

    abstract operator fun <B> invoke(f: (A) -> B): HK<F, B>

    fun lower(): HK<F, A> = invoke { a -> a }

    fun <B> map(ff: (A) -> B): Yoneda<F, B> =
            object : Yoneda<F, B>() {
                override fun <C> invoke(f: (B) -> C): HK<F, C> = this@Yoneda { f(ff(it)) }
            }

    fun toCoyoneda(): Coyoneda<F, A, A> = Coyoneda(lower(), listOf({ a: Any? -> a }))

    companion object {
        inline operator fun <reified U, A> invoke(fa: HK<U, A>, FF: Functor<U> = arrow.functor()): Yoneda<U, A> =
                object : Yoneda<U, A>() {
                    override fun <B> invoke(f: (A) -> B): HK<U, B> = FF.map(fa, f)
                }
    }
}