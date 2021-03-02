package arrow.core

const val HashDeprecation = "Hash is deprecated in favor of hashCode(), since Kotlin's Std doesn't take Hash into account"

/**
 * Wrapper type that caches a values precomputed hash with the value
 *
 * Provides a fast inequality check with its [Eq] instance and its [Hash] instance will use the cached hash.
 */
@Deprecated(HashDeprecation)
data class Hashed<A>(val hash: Int, val value: A)
