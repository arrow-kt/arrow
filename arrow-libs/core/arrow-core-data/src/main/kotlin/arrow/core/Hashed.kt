package arrow.core

import arrow.KindDeprecation
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation

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
@Deprecated(HashDeprecation)
data class Hashed<A>(val hash: Int, val value: A) : HashedOf<A> {
  companion object {
    fun <A> A.fromHash(HA: Hash<A>): Hashed<A> = Hashed(HA.run { this@fromHash.hash() }, this)
  }
}
