package kategory

typealias YonedaKind<F, A> = HK2<Yoneda.F, F, A>

typealias YonedaF<F> = HK<Yoneda.F, F>

fun <F, A> YonedaKind<F, A>.ev(): Yoneda<F, A> =
        this as Yoneda<F, A>

// FIXME using YonedaKind throws a compiler error, but not the expanded form
interface Yoneda<F, A> : HK<HK<Yoneda.F, F>, A> {

    class F private constructor()

    fun <B> apply(f: (A) -> B): HK<F, B>

    fun lower(): HK<F, A> =
            apply { a -> a }

    fun <B> map(ff: (A) -> B, FF: Functor<F>): Yoneda<F, B> =
            object : Yoneda<F, B> {
                override fun <C> apply(f: (B) -> C): HK<F, C> = this@Yoneda.apply({ f(ff(it)) })
            }

    fun toCoYoneda(): CoYoneda<F, A, A> =
            CoYoneda(lower(), listOf({ a: Any? -> a }))

    companion object {
        inline fun <reified U, A> apply(fa: HK<U, A>, FF: Functor<U> = functor()): Yoneda<U, A> =
                object : Yoneda<U, A> {
                    override fun <B> apply(f: (A) -> B): HK<U, B> = FF.map(fa, f)
                }

        fun <U> functor(FM: Functor<U>) = object : YonedaInstances<U> {
            override fun FM(): Functor<U> = FM
        }
    }
}