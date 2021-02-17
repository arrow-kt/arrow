package arrow.core.extensions.tuple4.order

import arrow.core.Tuple2
import arrow.core.Tuple4
import arrow.core.Tuple4.Companion
import arrow.core.extensions.Tuple4Order
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
fun <A, B, C, D> Tuple4<A, B, C, D>.compareTo(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  arg1: Tuple4<A, B, C, D>
): Int = arrow.core.Tuple4.order<A, B, C, D>(OA, OB, OC, OD).run {
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
fun <A, B, C, D> Tuple4<A, B, C, D>.eqv(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  arg1: Tuple4<A, B, C, D>
): Boolean = arrow.core.Tuple4.order<A, B, C, D>(OA, OB, OC, OD).run {
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
fun <A, B, C, D> Tuple4<A, B, C, D>.lt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  arg1: Tuple4<A, B, C, D>
): Boolean = arrow.core.Tuple4.order<A, B, C, D>(OA, OB, OC, OD).run {
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
fun <A, B, C, D> Tuple4<A, B, C, D>.lte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  arg1: Tuple4<A, B, C, D>
): Boolean = arrow.core.Tuple4.order<A, B, C, D>(OA, OB, OC, OD).run {
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
fun <A, B, C, D> Tuple4<A, B, C, D>.gt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  arg1: Tuple4<A, B, C, D>
): Boolean = arrow.core.Tuple4.order<A, B, C, D>(OA, OB, OC, OD).run {
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
fun <A, B, C, D> Tuple4<A, B, C, D>.gte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  arg1: Tuple4<A, B, C, D>
): Boolean = arrow.core.Tuple4.order<A, B, C, D>(OA, OB, OC, OD).run {
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
fun <A, B, C, D> Tuple4<A, B, C, D>.max(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  arg1: Tuple4<A, B, C, D>
): Tuple4<A, B, C, D> = arrow.core.Tuple4.order<A, B, C, D>(OA, OB, OC, OD).run {
  this@max.max(arg1) as arrow.core.Tuple4<A, B, C, D>
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
fun <A, B, C, D> Tuple4<A, B, C, D>.min(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  arg1: Tuple4<A, B, C, D>
): Tuple4<A, B, C, D> = arrow.core.Tuple4.order<A, B, C, D>(OA, OB, OC, OD).run {
  this@min.min(arg1) as arrow.core.Tuple4<A, B, C, D>
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
fun <A, B, C, D> Tuple4<A, B, C, D>.sort(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  arg1: Tuple4<A, B, C, D>
): Tuple2<Tuple4<A, B, C, D>, Tuple4<A, B, C, D>> = arrow.core.Tuple4.order<A, B, C,
  D>(OA, OB, OC, OD).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Tuple4<A, B, C, D>, arrow.core.Tuple4<A, B,
      C, D>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(OrderDeprecation)
inline fun <A, B, C, D> Companion.order(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>
): Tuple4Order<A, B, C, D> = object : arrow.core.extensions.Tuple4Order<A, B, C, D> {
  override fun
  OA(): arrow.typeclasses.Order<A> = OA

  override fun OB(): arrow.typeclasses.Order<B> = OB

  override fun OC(): arrow.typeclasses.Order<C> = OC

  override fun OD(): arrow.typeclasses.Order<D> = OD
}
