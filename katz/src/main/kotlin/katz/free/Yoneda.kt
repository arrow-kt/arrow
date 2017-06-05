package katz

typealias YonedaKind<U, A> = HK2<Yoneda.F, U, A>

typealias YonedaF<U> = HK<Yoneda.F, U>

fun <U, A> YonedaKind<U, A>.ev(): Yoneda<U, A> =
        this as Yoneda<U, A>

// FIXME using YonedaKind throws a compiler error, but not the expanded form
interface Yoneda<FU, A> : HK<HK<Yoneda.F, FU>, A> {

    class F private constructor()

    fun <B> apply(f: (A) -> B): HK<FU, B>

    fun lower(): HK<FU, A> =
            apply { a -> a }

    fun <B> map(ff: (A) -> B, FF: Functor<FU>): Yoneda<FU, B> =
            object : Yoneda<FU, B> {
                override fun <C> apply(f: (B) -> C): HK<FU, C> = this@Yoneda.apply({ f(ff(it)) })
            }

    fun toCoYoneda(): CoYoneda<FU, A, A> =
            CoYoneda({ it }, lower())

    companion object {
        inline fun <reified U, A> apply(fa: HK<U, A>, FF: Functor<U> = functor()): Yoneda<U, A> =
                object : Yoneda<U, A> {
                    override fun <B> apply(f: (A) -> B): HK<U, B> = FF.map(fa, f)
                }
    }
}