@file:Suppress("UNUSED_PARAMETER")

package arrow

interface Applicative<F> : Functor<F>, Typeclass {

    fun <A> pure(a: A): HK<F, A>

    fun <A, B> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B>

    fun <A, B> product(fa: HK<F, A>, fb: HK<F, B>): HK<F, Tuple2<A, B>> = ap(fb, map(fa) { a: A -> { b: B -> Tuple2(a, b) } })

    override fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B> = ap(fa, pure(f))

    fun <A, B, Z> map2(fa: HK<F, A>, fb: HK<F, B>, f: (Tuple2<A, B>) -> Z): HK<F, Z> = map(product(fa, fb), f)

    fun <A, B, Z> map2Eval(fa: HK<F, A>, fb: Eval<HK<F, B>>, f: (Tuple2<A, B>) -> Z): Eval<HK<F, Z>> = fb.map { fc -> map2(fa, fc, f) }
}

inline fun <reified F> applicative(): Applicative<F> = instance(InstanceParametrizedType(Applicative::class.java, listOf(typeLiteral<F>())))
