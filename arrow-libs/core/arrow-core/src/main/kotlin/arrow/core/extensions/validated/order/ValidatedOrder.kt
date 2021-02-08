package arrow.core.extensions.validated.order

import arrow.core.Tuple2
import arrow.core.Validated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedOrder
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation
import kotlin.Boolean
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
  OrderDeprecation,
  ReplaceWith("this.compareTo(arg1)", "arrow.core.compareTo")
)
fun <L, R> Validated<L, R>.compareTo(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Int = Validated.order(OL, OR).run {
  compareTo(arg1)
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
fun <L, R> Validated<L, R>.eqv(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Boolean = Validated.order(OL, OR).run {
  eqv(arg1)
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
fun <L, R> Validated<L, R>.lt(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Boolean = Validated.order(OL, OR).run {
  lt(arg1)
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
fun <L, R> Validated<L, R>.lte(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Boolean = Validated.order(OL, OR).run {
  lte(arg1)
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
fun <L, R> Validated<L, R>.gt(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Boolean = Validated.order(OL, OR).run {
  gt(arg1)
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
fun <L, R> Validated<L, R>.gte(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Boolean = Validated.order(OL, OR).run {
  gte(arg1)
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
  ReplaceWith("maxOf(this, arg1)")
)
fun <L, R> Validated<L, R>.max(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Validated<L, R> = Validated.order(OL, OR).run {
  max(arg1)
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
  ReplaceWith("minOf(this, arg1)")
)
fun <L, R> Validated<L, R>.min(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Validated<L, R> = Validated.order(OL, OR).run {
  min(arg1)
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
fun <L, R> Validated<L, R>.sort(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Tuple2<Validated<L, R>, Validated<L, R>> =
  Validated.order(OL, OR).run {
    sort(arg1)
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(OrderDeprecation)
inline fun <L, R> Companion.order(OL: Order<L>, OR: Order<R>): ValidatedOrder<L, R> = object :
  arrow.core.extensions.ValidatedOrder<L, R> {
  override fun OL(): arrow.typeclasses.Order<L> = OL

  override fun OR(): arrow.typeclasses.Order<R> = OR
}
