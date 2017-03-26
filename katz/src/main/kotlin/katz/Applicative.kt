package katz

interface Applicative<F> : Functor<F> {

    fun <A : Any> pure(a: A): HK<F, A>

    fun <A : Any, B : Any> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B>

    fun <A : Any, B : Any> product(fa: HK<F, A>, fb: HK<F, B>): HK<F, Pair<A, B>> =
            ap(fb, map(fa) { a: A -> { b: B -> Pair(a, b) } })

    override fun <A : Any, B : Any> map(fa: HK<F, A>, f: (A) -> B): HK<F, B> = ap(fa, pure(f))

    fun <A : Any, B : Any, Z : Any> map2(fa: HK<F, A>, fb: HK<F, B>, f: (Pair<A, B>) -> Z): HK<F, Z> =
            map(product(fa, fb), f)
}
