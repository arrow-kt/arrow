package arrow.fx.extensions.duration.semigroup

import arrow.fx.IODeprecation
import arrow.fx.extensions.DurationSemigroup
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Duration.Companion
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semigroup_singleton: DurationSemigroup = object : arrow.fx.extensions.DurationSemigroup
    {}

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
operator fun Duration.plus(arg1: Duration): Duration =
    arrow.fx.typeclasses.Duration.semigroup().run {
  this@plus.plus(arg1) as arrow.fx.typeclasses.Duration
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Duration.maybeCombine(arg1: Duration): Duration =
    arrow.fx.typeclasses.Duration.semigroup().run {
  this@maybeCombine.maybeCombine(arg1) as arrow.fx.typeclasses.Duration
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.semigroup(): DurationSemigroup = semigroup_singleton
