package arrow.fx.extensions.duration.order

import arrow.core.Tuple2
import arrow.fx.IODeprecation
import arrow.fx.extensions.DurationOrder
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Duration.Companion
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val order_singleton: DurationOrder = object : arrow.fx.extensions.DurationOrder {}

@JvmName("compareTo")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
operator fun Duration.compareTo(arg1: Duration): Int = arrow.fx.typeclasses.Duration.order().run {
  this@compareTo.compareTo(arg1) as kotlin.Int
}

@JvmName("eqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Duration.eqv(arg1: Duration): Boolean = arrow.fx.typeclasses.Duration.order().run {
  this@eqv.eqv(arg1) as kotlin.Boolean
}

@JvmName("lt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Duration.lt(arg1: Duration): Boolean = arrow.fx.typeclasses.Duration.order().run {
  this@lt.lt(arg1) as kotlin.Boolean
}

@JvmName("lte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Duration.lte(arg1: Duration): Boolean = arrow.fx.typeclasses.Duration.order().run {
  this@lte.lte(arg1) as kotlin.Boolean
}

@JvmName("gt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Duration.gt(arg1: Duration): Boolean = arrow.fx.typeclasses.Duration.order().run {
  this@gt.gt(arg1) as kotlin.Boolean
}

@JvmName("gte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Duration.gte(arg1: Duration): Boolean = arrow.fx.typeclasses.Duration.order().run {
  this@gte.gte(arg1) as kotlin.Boolean
}

@JvmName("max")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Duration.max(arg1: Duration): Duration = arrow.fx.typeclasses.Duration.order().run {
  this@max.max(arg1) as arrow.fx.typeclasses.Duration
}

@JvmName("min")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Duration.min(arg1: Duration): Duration = arrow.fx.typeclasses.Duration.order().run {
  this@min.min(arg1) as arrow.fx.typeclasses.Duration
}

@JvmName("sort")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(IODeprecation)
fun Duration.sort(arg1: Duration): Tuple2<Duration, Duration> =
  arrow.fx.typeclasses.Duration.order().run {
    this@sort.sort(arg1) as arrow.core.Tuple2<arrow.fx.typeclasses.Duration,
      arrow.fx.typeclasses.Duration>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(IODeprecation)
inline fun Companion.order(): DurationOrder = order_singleton
