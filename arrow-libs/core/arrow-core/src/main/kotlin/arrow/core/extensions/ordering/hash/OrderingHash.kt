package arrow.core.extensions.ordering.hash

import arrow.core.Ordering
import arrow.core.Ordering.Companion
import arrow.core.extensions.OrderingHash
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val hash_singleton: OrderingHash = object : arrow.core.extensions.OrderingHash {}

@JvmName("hashWithSalt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("hashWithSalt(arg1)"))
fun Ordering.hashWithSalt(arg1: Int): Int = arrow.core.Ordering.hash().run {
  this@hashWithSalt.hashWithSalt(arg1) as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Hash.ordering()", "arrow.core.Hash", "arrow.core.ordering"))
inline fun Companion.hash(): OrderingHash = hash_singleton
