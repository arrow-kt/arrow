package arrow.core.extensions.option.order

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.Tuple2
import arrow.core.extensions.OptionOrder
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
fun <A> Option<A>.compareTo(OA: Order<A>, arg1: Option<A>): Int =
    arrow.core.Option.order<A>(OA).run {
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
fun <A> Option<A>.eqv(OA: Order<A>, arg1: Option<A>): Boolean = arrow.core.Option.order<A>(OA).run {
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
fun <A> Option<A>.lt(OA: Order<A>, arg1: Option<A>): Boolean = arrow.core.Option.order<A>(OA).run {
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
fun <A> Option<A>.lte(OA: Order<A>, arg1: Option<A>): Boolean = arrow.core.Option.order<A>(OA).run {
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
fun <A> Option<A>.gt(OA: Order<A>, arg1: Option<A>): Boolean = arrow.core.Option.order<A>(OA).run {
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
fun <A> Option<A>.gte(OA: Order<A>, arg1: Option<A>): Boolean = arrow.core.Option.order<A>(OA).run {
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
fun <A> Option<A>.max(OA: Order<A>, arg1: Option<A>): Option<A> =
    arrow.core.Option.order<A>(OA).run {
  this@max.max(arg1) as arrow.core.Option<A>
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
fun <A> Option<A>.min(OA: Order<A>, arg1: Option<A>): Option<A> =
    arrow.core.Option.order<A>(OA).run {
  this@min.min(arg1) as arrow.core.Option<A>
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
fun <A> Option<A>.sort(OA: Order<A>, arg1: Option<A>): Tuple2<Option<A>, Option<A>> =
    arrow.core.Option.order<A>(OA).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Option<A>, arrow.core.Option<A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.order(OA: Order<A>): OptionOrder<A> = object :
    arrow.core.extensions.OptionOrder<A> { override fun OA(): arrow.typeclasses.Order<A> = OA }
