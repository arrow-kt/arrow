package arrow.fx.extensions.duration.monoid

import arrow.fx.IODeprecation
import arrow.fx.extensions.DurationMonoid
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Duration.Companion
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monoid_singleton: DurationMonoid = object : arrow.fx.extensions.DurationMonoid {}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Collection<Duration>.combineAll(): Duration = arrow.fx.typeclasses.Duration.monoid().run {
  this@combineAll.combineAll() as arrow.fx.typeclasses.Duration
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun combineAll(arg0: List<Duration>): Duration = arrow.fx.typeclasses.Duration
  .monoid()
  .combineAll(arg0) as arrow.fx.typeclasses.Duration

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.monoid(): DurationMonoid = monoid_singleton
