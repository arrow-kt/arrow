package arrow.typeclasses

import arrow.Kind
import arrow.Kind2

/**
 * https://www.youtube.com/watch?v=wvSP5qYiz4Y
 */
interface Nested<out F, out G>

typealias NestedType<F, G, A> = Kind<Nested<F, G>, A>

typealias UnnestedType<F, G, A> = Kind<F, Kind<G, A>>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> UnnestedType<F, G, A>.nest(): NestedType<F, G, A> = this as Kind<Nested<F, G>, A>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> NestedType<F, G, A>.unnest(): Kind<F, Kind<G, A>> = this as Kind<F, Kind<G, A>>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>>.binest(): Kind2<Nested<F, G>, A, B> = this as Kind2<Nested<F, G>, A, B>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> Kind2<Nested<F, G>, A, B>.biunnest(): Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>> = this as Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>>
