package arrow

/**
 * https://www.youtube.com/watch?v=wvSP5qYiz4Y
 */
interface Nested<out F, out G>

typealias NestedType<F, G, A> = HK<Nested<F, G>, A>

typealias UnnestedType<F, G, A> = HK<F, HK<G, A>>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> UnnestedType<F, G, A>.nest(): NestedType<F, G, A> = this as HK<Nested<F, G>, A>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> NestedType<F, G, A>.unnest(): HK<F, HK<G, A>> = this as HK<F, HK<G, A>>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> HK2<F, HK2<G, A, B>, HK2<G, A, B>>.binest(): HK2<Nested<F, G>, A, B> = this as HK2<Nested<F, G>, A, B>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> HK2<Nested<F, G>, A, B>.biunnest(): HK2<F, HK2<G, A, B>, HK2<G, A, B>> = this as HK2<F, HK2<G, A, B>, HK2<G, A, B>>
