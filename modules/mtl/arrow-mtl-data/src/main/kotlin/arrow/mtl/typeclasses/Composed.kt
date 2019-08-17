package arrow.mtl.typeclasses

import arrow.Kind
import arrow.Kind2

/**
 * A type to represent λ[α => Kind[F, G, α]]
 *
 * Use unnest to expand it, nest to re-compose it
 */
interface Nested<out F, out G>

typealias NestedType<F, G, A> = Kind<Nested<F, G>, A>

typealias UnnestedType<F, G, A> = Kind<F, Kind<G, A>>

typealias BinestedType<F, G, A, B> = Kind2<Nested<F, G>, A, B>

typealias BiunnestedType<F, G, A, B> = Kind2<F, Kind2<G, A, B>, Kind2<G, A, B>>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> UnnestedType<F, G, A>.nest(): NestedType<F, G, A> = this as NestedType<F, G, A>

@Suppress("UNCHECKED_CAST")
fun <F, G, A> NestedType<F, G, A>.unnest(): UnnestedType<F, G, A> = this as UnnestedType<F, G, A>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> BiunnestedType<F, G, A, B>.binest(): BinestedType<F, G, A, B> = this as BinestedType<F, G, A, B>

@Suppress("UNCHECKED_CAST")
fun <F, G, A, B> BinestedType<F, G, A, B>.biunnest(): BiunnestedType<F, G, A, B> = this as BiunnestedType<F, G, A, B>
