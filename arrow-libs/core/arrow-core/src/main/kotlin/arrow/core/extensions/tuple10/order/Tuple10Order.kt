package arrow.core.extensions.tuple10.order

import arrow.core.Tuple10
import arrow.core.Tuple10.Companion
import arrow.core.Tuple2
import arrow.core.extensions.Tuple10Order
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
  "compare(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ, arg1).toInt()",
  "arrow.core.compare"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.compareTo(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>,
  arg1: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Int = arrow.core.Tuple10.order<A, B, C, D, E, F, G, H, I,
    J>(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ).run {
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
  "compare(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ, arg1) == Ordering.EQ",
  "arrow.core.compare",
  "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.eqv(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>,
  arg1: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Boolean = arrow.core.Tuple10.order<A, B, C, D, E, F, G, H, I,
    J>(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ).run {
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
    "compare(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ, arg1) == Ordering.LT",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.lt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>,
  arg1: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Boolean = arrow.core.Tuple10.order<A, B, C, D, E, F, G, H, I,
    J>(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ).run {
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
    "compare(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ, arg1) != Ordering.GT",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.lte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>,
  arg1: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Boolean = arrow.core.Tuple10.order<A, B, C, D, E, F, G, H, I,
    J>(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ).run {
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
    "compare(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ, arg1) == Ordering.GT",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.gt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>,
  arg1: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Boolean = arrow.core.Tuple10.order<A, B, C, D, E, F, G, H, I,
    J>(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ).run {
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
    "compare(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ, arg1) != Ordering.LT",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.gte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>,
  arg1: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Boolean = arrow.core.Tuple10.order<A, B, C, D, E, F, G, H, I,
    J>(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ).run {
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
    "if(compare(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ, arg1) == Ordering.GT) this else arg1",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.max(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>,
  arg1: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Tuple10<A, B, C, D, E, F, G, H, I, J> = arrow.core.Tuple10.order<A, B, C, D, E, F, G, H, I,
    J>(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ).run {
  this@max.max(arg1) as arrow.core.Tuple10<A, B, C, D, E, F, G, H, I, J>
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
    "if(compare(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ, arg1) == Ordering.LT) this else arg1",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.min(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>,
  arg1: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Tuple10<A, B, C, D, E, F, G, H, I, J> = arrow.core.Tuple10.order<A, B, C, D, E, F, G, H, I,
    J>(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ).run {
  this@min.min(arg1) as arrow.core.Tuple10<A, B, C, D, E, F, G, H, I, J>
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
    "if(compare(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ, arg1) != Ordering.LT) Tuple2(this, b) else Tuple2(arg1, this)",
    "arrow.core.compare",
    "arrow.core.Ordering"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H, I, J> Tuple10<A, B, C, D, E, F, G, H, I, J>.sort(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>,
  arg1: Tuple10<A, B, C, D, E, F, G, H, I, J>
): Tuple2<Tuple10<A, B, C, D, E, F, G, H, I, J>, Tuple10<A, B, C, D, E, F, G, H, I, J>> =
    arrow.core.Tuple10.order<A, B, C, D, E, F, G, H, I,
    J>(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Tuple10<A, B, C, D, E, F, G, H, I, J>,
    arrow.core.Tuple10<A, B, C, D, E, F, G, H, I, J>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Order.tuple10(OA, OB, OC, OD, OE, OF, OG, OH, OI, OJ)",
    "arrow.core.Order",
    "arrow.core.tuple10"
  ),
  DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E, F, G, H, I, J> Companion.order(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  OJ: Order<J>
): Tuple10Order<A, B, C, D, E, F, G, H, I, J> = object : arrow.core.extensions.Tuple10Order<A, B, C,
    D, E, F, G, H, I, J> { override fun OA(): arrow.typeclasses.Order<A> = OA

  override fun OB(): arrow.typeclasses.Order<B> = OB

  override fun OC(): arrow.typeclasses.Order<C> = OC

  override fun OD(): arrow.typeclasses.Order<D> = OD

  override fun OE(): arrow.typeclasses.Order<E> = OE

  override fun OF(): arrow.typeclasses.Order<F> = OF

  override fun OG(): arrow.typeclasses.Order<G> = OG

  override fun OH(): arrow.typeclasses.Order<H> = OH

  override fun OI(): arrow.typeclasses.Order<I> = OI

  override fun OJ(): arrow.typeclasses.Order<J> = OJ }
