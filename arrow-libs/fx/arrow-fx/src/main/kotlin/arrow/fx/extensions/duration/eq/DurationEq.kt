package arrow.fx.extensions.duration.eq

import arrow.fx.IODeprecation
import arrow.fx.extensions.DurationEq
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Duration.Companion
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val eq_singleton: DurationEq = object : arrow.fx.extensions.DurationEq {}

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Duration.neqv(arg1: Duration): Boolean = arrow.fx.typeclasses.Duration.eq().run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.eq(): DurationEq = eq_singleton
