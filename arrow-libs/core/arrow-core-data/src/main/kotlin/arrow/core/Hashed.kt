package arrow.core

import arrow.typeclasses.Hash

class ForHashed private constructor() { companion object }
typealias HashedOf<A> = arrow.Kind<ForHashed, A>
typealias HashedKindedJ<A> = io.kindedj.Hk<ForHashed, A>
@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> HashedOf<A>.fix(): Hashed<A> =
  this as Hashed<A>

/**
 * Wrapper type that caches a values precomputed hash with the value
 *
 * Provides a fast inequality check with its [Eq] instance and its [Hash] instance will use the cached hash.
 */
data class Hashed<A>(val hash: Int, val value: A) : HashedOf<A> {
  companion object {
    fun <A> A.fromHash(HA: Hash<A>): Hashed<A> = Hashed(HA.run { this@fromHash.hash() }, this)
  }
}
