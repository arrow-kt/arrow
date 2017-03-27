package katz.typeclasses

interface Applicative<F> : Functor<F> {

    fun <A> pure(a: A): HK<F, A>

    fun <A, B> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B>

    fun <A, B> product(fa: HK<F, A>, fb: HK<F, B>): HK<F, Pair<A, B>> =
            ap(fb, map(fa) { a: A -> { b: B -> Pair(a, b) } })

    override fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B> = ap(fa, pure(f))

    fun <A, B, Z> map2(fa: HK<F, A>, fb: HK<F, B>, f: (Pair<A, B>) -> Z): HK<F, Z> =
            map(product(fa, fb), f)
}
