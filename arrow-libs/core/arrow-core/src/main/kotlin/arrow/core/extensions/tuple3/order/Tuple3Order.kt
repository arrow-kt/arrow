package arrow.core.extensions.tuple3.order

import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple3.Companion
import arrow.core.extensions.Tuple3Order
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
  "Tuple3 is deprecated in favor of Kotlin's Triple. Use Triple functionality.",
  ReplaceWith(
    "Triple(this.a, this.b, this.c).compare(OA, OB, OC, arg1).toInt()",
    "arrow.core.compare"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Tuple3<A, B, C>.compareTo(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  arg1: Tuple3<A, B, C>
): Int = arrow.core.Tuple3.order<A, B, C>(OA, OB, OC).run {
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
  "Tuple3 is deprecated in favor of Kotlin's Triple. Use Triple functionality.",
  ReplaceWith(
    "Triple(this.a, this.b, this.c).compare(OA, OB, OC, arg1) == Ordering.LT",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Tuple3<A, B, C>.eqv(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  arg1: Tuple3<A, B, C>
): Boolean = arrow.core.Tuple3.order<A, B, C>(OA, OB, OC).run {
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
  "Tuple3 is deprecated in favor of Kotlin's Triple. Use Triple functionality.",
  ReplaceWith(
    "Triple(this.a, this.b, this.c).compare(OA, OB, OC, arg1) == Ordering.LT",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Tuple3<A, B, C>.lt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  arg1: Tuple3<A, B, C>
): Boolean = arrow.core.Tuple3.order<A, B, C>(OA, OB, OC).run {
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
  "Tuple3 is deprecated in favor of Kotlin's Triple. Use Triple functionality.",
  ReplaceWith(
    "Triple(this.a, this.b, this.c).compare(OA, OB, OC, arg1) != Ordering.GT",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Tuple3<A, B, C>.lte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  arg1: Tuple3<A, B, C>
): Boolean = arrow.core.Tuple3.order<A, B, C>(OA, OB, OC).run {
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
  "Tuple3 is deprecated in favor of Kotlin's Triple. Use Triple functionality.",
  ReplaceWith(
    "Triple(this.a, this.b, this.c).compare(OA, OB, OC, arg1) == Ordering.GT",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Tuple3<A, B, C>.gt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  arg1: Tuple3<A, B, C>
): Boolean = arrow.core.Tuple3.order<A, B, C>(OA, OB, OC).run {
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
  "Tuple3 is deprecated in favor of Kotlin's Triple. Use Triple functionality.",
  ReplaceWith(
    "Triple(this.a, this.b, this.c).compare(OA, OB, OC, arg1) != Ordering.LT",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Tuple3<A, B, C>.gte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  arg1: Tuple3<A, B, C>
): Boolean = arrow.core.Tuple3.order<A, B, C>(OA, OB, OC).run {
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
  "Tuple3 is deprecated in favor of Kotlin's Triple. Use Triple functionality.",
  ReplaceWith(
    "if(Triple(this.a, this.b, this.c).compare(OA, OB, OC, arg1) == Ordering.GT) this else arg1",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Tuple3<A, B, C>.max(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  arg1: Tuple3<A, B, C>
): Tuple3<A, B, C> = arrow.core.Tuple3.order<A, B, C>(OA, OB, OC).run {
  this@max.max(arg1) as arrow.core.Tuple3<A, B, C>
}

@JvmName("min")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Tuple3 is deprecated in favor of Kotlin's Triple. Use Triple functionality.",
  ReplaceWith(
    "if(Triple(this.a, this.b, this.c).compare(OA, OB, OC, arg1) == Ordering.LT) this else arg1",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Tuple3<A, B, C>.min(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  arg1: Tuple3<A, B, C>
): Tuple3<A, B, C> = arrow.core.Tuple3.order<A, B, C>(OA, OB, OC).run {
  this@min.min(arg1) as arrow.core.Tuple3<A, B, C>
}

@JvmName("sort")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Tuple3 is deprecated in favor of Kotlin's Triple. Use Triple functionality.",
  ReplaceWith(
    "if(Triple(this.a, this.b, this.c).compare(OA, OB, OC, arg1) != Ordering.LT) Tuple2(this, b) else Tuple2(arg1, this)",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Tuple3<A, B, C>.sort(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  arg1: Tuple3<A, B, C>
): Tuple2<Tuple3<A, B, C>, Tuple3<A, B, C>> = arrow.core.Tuple3.order<A, B, C>(OA, OB, OC).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Tuple3<A, B, C>, arrow.core.Tuple3<A, B, C>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Tuple3 is deprecated in favor of Kotlin's Triple. Use Triple functionality.",
  ReplaceWith(
    "Order.triple(OA, OB, OC)",
    "arrow.core.Order",
    "arrow.core.triple"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C> Companion.order(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>
): Tuple3Order<A, B, C> = object : arrow.core.extensions.Tuple3Order<A, B, C> { override fun OA():
    arrow.typeclasses.Order<A> = OA

  override fun OB(): arrow.typeclasses.Order<B> = OB

  override fun OC(): arrow.typeclasses.Order<C> = OC }
