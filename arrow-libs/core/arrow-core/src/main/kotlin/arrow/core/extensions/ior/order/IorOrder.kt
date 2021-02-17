package arrow.core.extensions.ior.order

import arrow.core.Ior
import arrow.core.Ior.Companion
import arrow.core.Tuple2
import arrow.core.extensions.IorOrder
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation

@JvmName("compareTo")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  OrderDeprecation,
  ReplaceWith("this.compareTo(arg1)", "arrow.core.compareTo")
)
fun <L, R> Ior<L, R>.compareTo(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Int = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  OrderDeprecation,
  ReplaceWith("this == arg1")
)
fun <L, R> Ior<L, R>.eqv(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Boolean = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  OrderDeprecation,
  ReplaceWith("this < arg1", "arrow.core.compareTo")
)
fun <L, R> Ior<L, R>.lt(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Boolean = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  OrderDeprecation,
  ReplaceWith("this <= arg1", "arrow.core.compareTo")
)
fun <L, R> Ior<L, R>.lte(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Boolean = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  OrderDeprecation,
  ReplaceWith("this > arg1", "arrow.core.compareTo")
)
fun <L, R> Ior<L, R>.gt(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Boolean = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  OrderDeprecation,
  ReplaceWith("this >= arg1", "arrow.core.compareTo")
)
fun <L, R> Ior<L, R>.gte(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Boolean = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  OrderDeprecation,
  ReplaceWith("maxOf(this,arg1)")
)
fun <L, R> Ior<L, R>.max(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Ior<L, R> = arrow.core.Ior.order<L, R>(OL, OR).run {
  this@max.max(arg1) as arrow.core.Ior<L, R>
}

@JvmName("min")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  OrderDeprecation,
  ReplaceWith("minOf(this,arg1)")
)
fun <L, R> Ior<L, R>.min(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Ior<L, R> = arrow.core.Ior.order<L, R>(OL, OR).run {
  this@min.min(arg1) as arrow.core.Ior<L, R>
}

@JvmName("sort")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  OrderDeprecation,
  ReplaceWith("sort(this, arg1).let { (a, b) -> Tuple2(b, a) }", "arrow.core.Tuple2", "arrow.core.sort")
)
fun <L, R> Ior<L, R>.sort(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Tuple2<Ior<L, R>, Ior<L, R>> = arrow.core.Ior.order<L, R>(OL, OR).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Ior<L, R>, arrow.core.Ior<L, R>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(OrderDeprecation)
inline fun <L, R> Companion.order(OL: Order<L>, OR: Order<R>): IorOrder<L, R> = object :
  arrow.core.extensions.IorOrder<L, R> {
  override fun OL(): arrow.typeclasses.Order<L> = OL

  override fun OR(): arrow.typeclasses.Order<R> = OR
}
