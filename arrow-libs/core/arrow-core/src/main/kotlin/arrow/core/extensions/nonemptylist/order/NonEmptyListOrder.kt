package arrow.core.extensions.nonemptylist.order

import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.core.Tuple2
import arrow.core.extensions.NonEmptyListOrder
import arrow.typeclasses.Order
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Int
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("compareTo")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "compareTo(OA, arg1)",
  "arrow.core.compareTo"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList<A>.compareTo(OA: Order<A>, arg1: NonEmptyList<A>): Int =
    arrow.core.NonEmptyList.order<A>(OA).run {
  this@compareTo.compareTo(arg1) as kotlin.Int
}

@JvmName("eqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "eqv(OA, arg1)",
  "arrow.core.eqv"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList<A>.eqv(OA: Order<A>, arg1: NonEmptyList<A>): Boolean =
    arrow.core.NonEmptyList.order<A>(OA).run {
  this@eqv.eqv(arg1) as kotlin.Boolean
}

@JvmName("lt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "lt(OA, arg1)",
  "arrow.core.lt"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList<A>.lt(OA: Order<A>, arg1: NonEmptyList<A>): Boolean =
    arrow.core.NonEmptyList.order<A>(OA).run {
  this@lt.lt(arg1) as kotlin.Boolean
}

@JvmName("lte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "lte(OA, arg1)",
  "arrow.core.lte"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList<A>.lte(OA: Order<A>, arg1: NonEmptyList<A>): Boolean =
    arrow.core.NonEmptyList.order<A>(OA).run {
  this@lte.lte(arg1) as kotlin.Boolean
}

@JvmName("gt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "gt(OA, arg1)",
  "arrow.core.gt"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList<A>.gt(OA: Order<A>, arg1: NonEmptyList<A>): Boolean =
    arrow.core.NonEmptyList.order<A>(OA).run {
  this@gt.gt(arg1) as kotlin.Boolean
}

@JvmName("gte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "gte(OA, arg1)",
  "arrow.core.gte"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList<A>.gte(OA: Order<A>, arg1: NonEmptyList<A>): Boolean =
    arrow.core.NonEmptyList.order<A>(OA).run {
  this@gte.gte(arg1) as kotlin.Boolean
}

@JvmName("max")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "max(OA, arg1)",
  "arrow.core.max"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList<A>.max(OA: Order<A>, arg1: NonEmptyList<A>): NonEmptyList<A> =
    arrow.core.NonEmptyList.order<A>(OA).run {
  this@max.max(arg1) as arrow.core.NonEmptyList<A>
}

@JvmName("min")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "min(OA, arg1)",
  "arrow.core.min"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList<A>.min(OA: Order<A>, arg1: NonEmptyList<A>): NonEmptyList<A> =
    arrow.core.NonEmptyList.order<A>(OA).run {
  this@min.min(arg1) as arrow.core.NonEmptyList<A>
}

@JvmName("sort")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "sort(OA, arg1)",
  "arrow.core.sort"
  ),
  DeprecationLevel.WARNING
)
fun <A> NonEmptyList<A>.sort(OA: Order<A>, arg1: NonEmptyList<A>): Tuple2<NonEmptyList<A>,
    NonEmptyList<A>> = arrow.core.NonEmptyList.order<A>(OA).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.NonEmptyList<A>, arrow.core.NonEmptyList<A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "Order.nonEmptyList(OA)",
    "arrow.core.nonEmptyList", "arrow.core.Order"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.order(OA: Order<A>): NonEmptyListOrder<A> = object :
    arrow.core.extensions.NonEmptyListOrder<A> { override fun OA(): arrow.typeclasses.Order<A> = OA
    }
