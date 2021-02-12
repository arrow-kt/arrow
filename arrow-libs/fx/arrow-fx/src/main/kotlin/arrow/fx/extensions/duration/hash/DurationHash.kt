package arrow.fx.extensions.duration.hash

import arrow.fx.IODeprecation
import arrow.fx.extensions.DurationHash
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Duration.Companion
import kotlin.Deprecated
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val hash_singleton: DurationHash = object : arrow.fx.extensions.DurationHash {}

@JvmName("hashWithSalt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Duration.hashWithSalt(arg1: Int): Int = arrow.fx.typeclasses.Duration.hash().run {
  this@hashWithSalt.hashWithSalt(arg1) as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.hash(): DurationHash = hash_singleton
