package arrow.core

import arrow.KindDeprecation

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
class ForHashed private constructor() { companion object }

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias HashedOf<A> = arrow.Kind<ForHashed, A>

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias HashedKindedJ<A> = io.kindedj.Hk<ForHashed, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
inline fun <A> HashedOf<A>.fix(): Hashed<A> =
  this as Hashed<A>

/**
 * Wrapper type that caches a values precomputed hash with the value
 *
 * Provides a fast inequality check with its [Eq] instance and its [Hash] instance will use the cached hash.
 */
data class Hashed<A>(val hash: Int, val value: A) : HashedOf<A>
