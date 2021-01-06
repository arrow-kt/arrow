package arrow.core.extensions.listk.order

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Ordering
import arrow.core.Tuple2
import arrow.core.extensions.ListKOrder
import arrow.typeclasses.Order
import kotlin.Boolean
import kotlin.Int
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("compare")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("compare(OA, arg1)", "arrow.core.compare"))
fun <A> Kind<ForListK, A>.compare(OA: Order<A>, arg1: Kind<ForListK, A>): Ordering =
    arrow.core.ListK.order<A>(OA).run {
  this@compare.compare(arg1) as arrow.core.Ordering
}

@JvmName("compareTo")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("compareTo(OA, arg1)", "arrow.core.compareTo"))
fun <A> Kind<ForListK, A>.compareTo(OA: Order<A>, arg1: Kind<ForListK, A>): Int =
    arrow.core.ListK.order<A>(OA).run {
  this@compareTo.compareTo(arg1) as kotlin.Int
}

@JvmName("eqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("eqv(OA, arg1)", "arrow.core.eqv"))
fun <A> Kind<ForListK, A>.eqv(OA: Order<A>, arg1: Kind<ForListK, A>): Boolean =
    arrow.core.ListK.order<A>(OA).run {
  this@eqv.eqv(arg1) as kotlin.Boolean
}

@JvmName("lt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("lt(OA, arg1)", "arrow.core.lt"))
fun <A> Kind<ForListK, A>.lt(OA: Order<A>, arg1: Kind<ForListK, A>): Boolean =
    arrow.core.ListK.order<A>(OA).run {
  this@lt.lt(arg1) as kotlin.Boolean
}

@JvmName("lte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("lte(OA, arg1)", "arrow.core.lte"))
fun <A> Kind<ForListK, A>.lte(OA: Order<A>, arg1: Kind<ForListK, A>): Boolean =
    arrow.core.ListK.order<A>(OA).run {
  this@lte.lte(arg1) as kotlin.Boolean
}

@JvmName("gt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("gt(OA, arg1)", "arrow.core.gt"))
fun <A> Kind<ForListK, A>.gt(OA: Order<A>, arg1: Kind<ForListK, A>): Boolean =
    arrow.core.ListK.order<A>(OA).run {
  this@gt.gt(arg1) as kotlin.Boolean
}

@JvmName("gte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("gte(OA, arg1)", "arrow.core.gte"))
fun <A> Kind<ForListK, A>.gte(OA: Order<A>, arg1: Kind<ForListK, A>): Boolean =
    arrow.core.ListK.order<A>(OA).run {
  this@gte.gte(arg1) as kotlin.Boolean
}

@JvmName("max")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("max(OA, arg1)", "arrow.core.max"))
fun <A> Kind<ForListK, A>.max(OA: Order<A>, arg1: Kind<ForListK, A>): ListK<A> =
    arrow.core.ListK.order<A>(OA).run {
  this@max.max(arg1) as arrow.core.ListK<A>
}

@JvmName("min")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("min(OA, arg1)", "arrow.core.min"))
fun <A> Kind<ForListK, A>.min(OA: Order<A>, arg1: Kind<ForListK, A>): ListK<A> =
    arrow.core.ListK.order<A>(OA).run {
  this@min.min(arg1) as arrow.core.ListK<A>
}

@JvmName("sort")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("sort(OA, arg1)", "arrow.core.sort"))
fun <A> Kind<ForListK, A>.sort(OA: Order<A>, arg1: Kind<ForListK, A>): Tuple2<Kind<ForListK, A>,
    Kind<ForListK, A>> = arrow.core.ListK.order<A>(OA).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>,
    arrow.Kind<arrow.core.ForListK, A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("listOrder(OA)", "arrow.core.listOrder"))
inline fun <A> Companion.order(OA: Order<A>): ListKOrder<A> = object :
    arrow.core.extensions.ListKOrder<A> { override fun OA(): arrow.typeclasses.Order<A> = OA }
