package katz

typealias CoYonedaKind<U, P, A> = HK3<CoYoneda.F, U, P, A>

typealias CoYonedaF<U, P> = HK2<CoYoneda.F, U, P>

fun <U, A, B> CoYonedaKind<U, A, B>.ev(): CoYoneda<U, A, B> =
        this as CoYoneda<U, A, B>

data class CoYoneda<FU, P, A>(val function: (P) -> A, val pivot: HK<FU, P>) : CoYonedaKind<FU, P, A> {
    class F private constructor()

    fun lower(FF: Functor<FU>): HK<FU, A> =
            FF.map(pivot, function)

    fun <B> map(f: (A) -> B): CoYoneda<FU, P, B> =
            CoYoneda({ f(function(it)) }, pivot)

    fun toYoneda(FF: Functor<FU>): Yoneda<FU, A> =
            object : Yoneda<FU, A> {
                override fun <B> apply(f: (A) -> B): HK<FU, B> = map(f).lower(FF)
            }

    companion object {
        inline fun <reified U, A, B> apply(fa: HK<U, A>, noinline f: (A) -> B): CoYoneda<U, A, B> =
                CoYoneda(f, fa)
    }

}
