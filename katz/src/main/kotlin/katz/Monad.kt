package katz

interface Monad<F> : Applicative<F> {

    fun <A : Any, B : Any> flatMap(fa: HK<F, A>, f: (A) -> HK<F, B>): HK<F, B>

    override fun <A : Any, B : Any> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B> =
            flatMap(ff, { f -> map(fa, f) })
}

