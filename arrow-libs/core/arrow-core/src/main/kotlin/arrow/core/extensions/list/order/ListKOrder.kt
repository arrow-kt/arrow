package arrow.core.extensions.list.order

import arrow.core.Ordering
import arrow.core.extensions.ListKOrder
import arrow.typeclasses.Order
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("compare")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("compare(OA, arg1)", "arrow.core.compare"))
fun <A> List<A>.compare(OA: Order<A>, arg1: List<A>): Ordering =
    arrow.core.extensions.list.order.List.order<A>(OA).run {
  arrow.core.ListK(this@compare).compare(arrow.core.ListK(arg1)) as arrow.core.Ordering
}

@JvmName("compareTo")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("compareTo(OA, arg1)", "arrow.core.compareTo"))
fun <A> List<A>.compareTo(OA: Order<A>, arg1: List<A>): Int =
    arrow.core.extensions.list.order.List.order<A>(OA).run {
  arrow.core.ListK(this@compareTo).compareTo(arrow.core.ListK(arg1)) as kotlin.Int
}

@JvmName("eqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("eqv(OA, arg1)", "arrow.core.eqv"))
fun <A> List<A>.eqv(OA: Order<A>, arg1: List<A>): Boolean =
    arrow.core.extensions.list.order.List.order<A>(OA).run {
  arrow.core.ListK(this@eqv).eqv(arrow.core.ListK(arg1)) as kotlin.Boolean
}

@JvmName("lt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("lt(OA, arg1)", "arrow.core.lt"))
fun <A> List<A>.lt(OA: Order<A>, arg1: List<A>): Boolean =
    arrow.core.extensions.list.order.List.order<A>(OA).run {
  arrow.core.ListK(this@lt).lt(arrow.core.ListK(arg1)) as kotlin.Boolean
}

@JvmName("lte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("lte(OA, arg1)", "arrow.core.lte"))
fun <A> List<A>.lte(OA: Order<A>, arg1: List<A>): Boolean =
    arrow.core.extensions.list.order.List.order<A>(OA).run {
  arrow.core.ListK(this@lte).lte(arrow.core.ListK(arg1)) as kotlin.Boolean
}

@JvmName("gt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("gt(OA, arg1)", "arrow.core.gt"))
fun <A> List<A>.gt(OA: Order<A>, arg1: List<A>): Boolean =
    arrow.core.extensions.list.order.List.order<A>(OA).run {
  arrow.core.ListK(this@gt).gt(arrow.core.ListK(arg1)) as kotlin.Boolean
}

@JvmName("gte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("gte(OA, arg1)", "arrow.core.gte"))
fun <A> List<A>.gte(OA: Order<A>, arg1: List<A>): Boolean =
    arrow.core.extensions.list.order.List.order<A>(OA).run {
  arrow.core.ListK(this@gte).gte(arrow.core.ListK(arg1)) as kotlin.Boolean
}

@JvmName("max")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("max(OA, arg1)", "arrow.core.max"))
fun <A> List<A>.max(OA: Order<A>, arg1: List<A>): List<A> =
    arrow.core.extensions.list.order.List.order<A>(OA).run {
  arrow.core.ListK(this@max).max(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
}

@JvmName("min")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("min(OA, arg1)", "arrow.core.min"))
fun <A> List<A>.min(OA: Order<A>, arg1: List<A>): List<A> =
    arrow.core.extensions.list.order.List.order<A>(OA).run {
  arrow.core.ListK(this@min).min(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("@extension projected functions are deprecated", ReplaceWith("listOrder(OA)", "arrow.core.listOrder"))
  inline fun <A> order(OA: Order<A>): ListKOrder<A> = object : arrow.core.extensions.ListKOrder<A> {
      override fun OA(): arrow.typeclasses.Order<A> = OA }}
