package arrow.core.extensions.tuple2.order

import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Order
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation
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
  OrderDeprecation,
  ReplaceWith("this.compareTo(arg1)", "arrow.core.compareTo")
)
fun <A, B> Tuple2<A, B>.compareTo(
  OA: Order<A>,
  OB: Order<B>,
  arg1: Tuple2<A, B>
): Int = arrow.core.Tuple2.order<A, B>(OA, OB).run {
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
fun <A, B> Tuple2<A, B>.eqv(
  OA: Order<A>,
  OB: Order<B>,
  arg1: Tuple2<A, B>
): Boolean = arrow.core.Tuple2.order<A, B>(OA, OB).run {
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
fun <A, B> Tuple2<A, B>.lt(
  OA: Order<A>,
  OB: Order<B>,
  arg1: Tuple2<A, B>
): Boolean = arrow.core.Tuple2.order<A, B>(OA, OB).run {
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
fun <A, B> Tuple2<A, B>.lte(
  OA: Order<A>,
  OB: Order<B>,
  arg1: Tuple2<A, B>
): Boolean = arrow.core.Tuple2.order<A, B>(OA, OB).run {
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
fun <A, B> Tuple2<A, B>.gt(
  OA: Order<A>,
  OB: Order<B>,
  arg1: Tuple2<A, B>
): Boolean = arrow.core.Tuple2.order<A, B>(OA, OB).run {
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
fun <A, B> Tuple2<A, B>.gte(
  OA: Order<A>,
  OB: Order<B>,
  arg1: Tuple2<A, B>
): Boolean = arrow.core.Tuple2.order<A, B>(OA, OB).run {
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
fun <A, B> Tuple2<A, B>.max(
  OA: Order<A>,
  OB: Order<B>,
  arg1: Tuple2<A, B>
): Tuple2<A, B> = arrow.core.Tuple2.order<A, B>(OA, OB).run {
  this@max.max(arg1) as arrow.core.Tuple2<A, B>
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
fun <A, B> Tuple2<A, B>.min(
  OA: Order<A>,
  OB: Order<B>,
  arg1: Tuple2<A, B>
): Tuple2<A, B> = arrow.core.Tuple2.order<A, B>(OA, OB).run {
  this@min.min(arg1) as arrow.core.Tuple2<A, B>
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
fun <A, B> Tuple2<A, B>.sort(
  OA: Order<A>,
  OB: Order<B>,
  arg1: Tuple2<A, B>
): Tuple2<Tuple2<A, B>, Tuple2<A, B>> = arrow.core.Tuple2.order<A, B>(OA, OB).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Tuple2<A, B>, arrow.core.Tuple2<A, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(OrderDeprecation)
inline fun <A, B> Companion.order(OA: Order<A>, OB: Order<B>): Tuple2Order<A, B> = object :
    arrow.core.extensions.Tuple2Order<A, B> { override fun OA(): arrow.typeclasses.Order<A> = OA

  override fun OB(): arrow.typeclasses.Order<B> = OB }
