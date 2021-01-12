package arrow.core.extensions.ordering.order

import arrow.core.Ordering
import arrow.core.Ordering.Companion
import arrow.core.Tuple2
import arrow.core.extensions.OrderingOrder
import kotlin.Boolean
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val order_singleton: OrderingOrder = object : arrow.core.extensions.OrderingOrder {}

@JvmName("compareTo")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("compareTo(arg1)"))
operator fun Ordering.compareTo(arg1: Ordering): Int = arrow.core.Ordering.order().run {
  this@compareTo.compareTo(arg1) as kotlin.Int
}

@JvmName("eqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("eqv(arg1)"))
fun Ordering.eqv(arg1: Ordering): Boolean = arrow.core.Ordering.order().run {
  this@eqv.eqv(arg1) as kotlin.Boolean
}

@JvmName("lt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("lt(arg1)"))
fun Ordering.lt(arg1: Ordering): Boolean = arrow.core.Ordering.order().run {
  this@lt.lt(arg1) as kotlin.Boolean
}

@JvmName("lte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("lte(arg1)"))
fun Ordering.lte(arg1: Ordering): Boolean = arrow.core.Ordering.order().run {
  this@lte.lte(arg1) as kotlin.Boolean
}

@JvmName("gt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("gt(arg1)"))
fun Ordering.gt(arg1: Ordering): Boolean = arrow.core.Ordering.order().run {
  this@gt.gt(arg1) as kotlin.Boolean
}

@JvmName("gte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("gte(arg1)"))
fun Ordering.gte(arg1: Ordering): Boolean = arrow.core.Ordering.order().run {
  this@gte.gte(arg1) as kotlin.Boolean
}

@JvmName("max")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("max(arg1)"))
fun Ordering.max(arg1: Ordering): Ordering = arrow.core.Ordering.order().run {
  this@max.max(arg1) as arrow.core.Ordering
}

@JvmName("min")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("min(arg1)"))
fun Ordering.min(arg1: Ordering): Ordering = arrow.core.Ordering.order().run {
  this@min.min(arg1) as arrow.core.Ordering
}

@JvmName("sort")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("sort(arg1)"))
fun Ordering.sort(arg1: Ordering): Tuple2<Ordering, Ordering> = arrow.core.Ordering.order().run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Ordering, arrow.core.Ordering>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Order.ordering()", "arrow.core.Order", "arrow.core.ordering"))
inline fun Companion.order(): OrderingOrder = order_singleton
