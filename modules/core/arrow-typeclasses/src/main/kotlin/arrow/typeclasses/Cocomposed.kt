package arrow.typeclasses

import arrow.Kind

/**
 * A type to represent λ[α => Kind[F, α, C]]
 *
 * Use counnest to expand it, conest to re-compose it
 */
interface Conested<out F, out B>

typealias ConestedType<F, A, B> = Kind<Conested<F, B>, A>

typealias CounnestedType<F, A, B> = Kind<Kind<F, A>, B>

@Suppress("UNCHECKED_CAST")
fun <F, A, B> CounnestedType<F, A, B>.conest(): ConestedType<F, A, B> = this as ConestedType<F, A, B>

@Suppress("UNCHECKED_CAST")
fun <F, A, B> ConestedType<F, A, B>.counnest(): CounnestedType<F, A, B> = this as CounnestedType<F, A, B>
