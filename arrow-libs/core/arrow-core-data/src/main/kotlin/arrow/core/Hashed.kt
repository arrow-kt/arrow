package arrow.core

import arrow.higherkind
import arrow.typeclasses.Hash

/**
 * Wrapper type that caches a values precomputed hash with the value
 *
 * Provides a fast inequality check with its [Eq] instance and its [Hash] instance will use the cached hash.
 */
@higherkind
data class Hashed<A>(val hash: Int, val value: A) : HashedOf<A> {
  companion object {
    fun <A> A.fromHash(HA: Hash<A>): Hashed<A> = Hashed(HA.run { this@fromHash.hash() }, this)
  }
}
