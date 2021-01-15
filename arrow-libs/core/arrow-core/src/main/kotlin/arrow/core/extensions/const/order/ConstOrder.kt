package arrow.core.extensions.const.order

import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.Tuple2
import arrow.core.extensions.ConstOrder
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
  "compareTo(ORD, arg1)",
  "arrow.core.compareTo"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.compareTo(ORD: Order<A>, arg1: Const<A, T>): Int = arrow.core.Const.order<A,
    T>(ORD).run {
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
  "eqv(ORD, arg1)",
  "arrow.core.eqv"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.eqv(ORD: Order<A>, arg1: Const<A, T>): Boolean = arrow.core.Const.order<A,
    T>(ORD).run {
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
  "lt(ORD, arg1)",
  "arrow.core.lt"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.lt(ORD: Order<A>, arg1: Const<A, T>): Boolean = arrow.core.Const.order<A,
    T>(ORD).run {
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
  "lte(ORD, arg1)",
  "arrow.core.lte"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.lte(ORD: Order<A>, arg1: Const<A, T>): Boolean = arrow.core.Const.order<A,
    T>(ORD).run {
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
  "gt(ORD, arg1)",
  "arrow.core.gt"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.gt(ORD: Order<A>, arg1: Const<A, T>): Boolean = arrow.core.Const.order<A,
    T>(ORD).run {
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
  "gte(ORD, arg1)",
  "arrow.core.gte"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.gte(ORD: Order<A>, arg1: Const<A, T>): Boolean = arrow.core.Const.order<A,
    T>(ORD).run {
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
  "max(ORD, arg1)",
  "arrow.core.max"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.max(ORD: Order<A>, arg1: Const<A, T>): Const<A, T> =
    arrow.core.Const.order<A, T>(ORD).run {
  this@max.max(arg1) as arrow.core.Const<A, T>
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
  "min(ORD, arg1)",
  "arrow.core.min"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.min(ORD: Order<A>, arg1: Const<A, T>): Const<A, T> =
    arrow.core.Const.order<A, T>(ORD).run {
  this@min.min(arg1) as arrow.core.Const<A, T>
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
  "sort(ORD, arg1)",
  "arrow.core.sort"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.sort(ORD: Order<A>, arg1: Const<A, T>): Tuple2<Const<A, T>, Const<A, T>> =
    arrow.core.Const.order<A, T>(ORD).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Const<A, T>, arrow.core.Const<A, T>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, T> Companion.order(ORD: Order<A>): ConstOrder<A, T> = object :
    arrow.core.extensions.ConstOrder<A, T> { override fun ORD(): arrow.typeclasses.Order<A> = ORD }
