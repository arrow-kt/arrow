package arrow.core.extensions.hashed.hash

import arrow.core.Hashed.Companion
import arrow.core.extensions.HashedHash
import kotlin.Any
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val hash_singleton: HashedHash<Any?> = object : HashedHash<Any?> {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.hash(): HashedHash<A> = hash_singleton as
    arrow.core.extensions.HashedHash<A>
