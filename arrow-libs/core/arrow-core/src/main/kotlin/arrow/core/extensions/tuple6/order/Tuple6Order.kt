package arrow.core.extensions.tuple6.order

import arrow.core.Tuple2
import arrow.core.Tuple6
import arrow.core.Tuple6.Companion
import arrow.core.extensions.Tuple6Order
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
  "compareTo(OA, OB, OC, OD, OE, OF, arg1)",
  "arrow.core.compareTo"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.compareTo(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  arg1: Tuple6<A, B, C, D, E, F>
): Int = arrow.core.Tuple6.order<A, B, C, D, E, F>(OA, OB, OC, OD, OE, OF).run {
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
  "eqv(OA, OB, OC, OD, OE, OF, arg1)",
  "arrow.core.eqv"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.eqv(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  arg1: Tuple6<A, B, C, D, E, F>
): Boolean = arrow.core.Tuple6.order<A, B, C, D, E, F>(OA, OB, OC, OD, OE, OF).run {
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
  "lt(OA, OB, OC, OD, OE, OF, arg1)",
  "arrow.core.lt"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.lt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  arg1: Tuple6<A, B, C, D, E, F>
): Boolean = arrow.core.Tuple6.order<A, B, C, D, E, F>(OA, OB, OC, OD, OE, OF).run {
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
  "lte(OA, OB, OC, OD, OE, OF, arg1)",
  "arrow.core.lte"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.lte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  arg1: Tuple6<A, B, C, D, E, F>
): Boolean = arrow.core.Tuple6.order<A, B, C, D, E, F>(OA, OB, OC, OD, OE, OF).run {
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
  "gt(OA, OB, OC, OD, OE, OF, arg1)",
  "arrow.core.gt"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.gt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  arg1: Tuple6<A, B, C, D, E, F>
): Boolean = arrow.core.Tuple6.order<A, B, C, D, E, F>(OA, OB, OC, OD, OE, OF).run {
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
  "gte(OA, OB, OC, OD, OE, OF, arg1)",
  "arrow.core.gte"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.gte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  arg1: Tuple6<A, B, C, D, E, F>
): Boolean = arrow.core.Tuple6.order<A, B, C, D, E, F>(OA, OB, OC, OD, OE, OF).run {
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
  "max(OA, OB, OC, OD, OE, OF, arg1)",
  "arrow.core.max"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.max(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  arg1: Tuple6<A, B, C, D, E, F>
): Tuple6<A, B, C, D, E, F> = arrow.core.Tuple6.order<A, B, C, D, E,
    F>(OA, OB, OC, OD, OE, OF).run {
  this@max.max(arg1) as arrow.core.Tuple6<A, B, C, D, E, F>
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
  "min(OA, OB, OC, OD, OE, OF, arg1)",
  "arrow.core.min"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.min(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  arg1: Tuple6<A, B, C, D, E, F>
): Tuple6<A, B, C, D, E, F> = arrow.core.Tuple6.order<A, B, C, D, E,
    F>(OA, OB, OC, OD, OE, OF).run {
  this@min.min(arg1) as arrow.core.Tuple6<A, B, C, D, E, F>
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
  "sort(OA, OB, OC, OD, OE, OF, arg1)",
  "arrow.core.sort"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.sort(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  arg1: Tuple6<A, B, C, D, E, F>
): Tuple2<Tuple6<A, B, C, D, E, F>, Tuple6<A, B, C, D, E, F>> = arrow.core.Tuple6.order<A, B, C, D,
    E, F>(OA, OB, OC, OD, OE, OF).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Tuple6<A, B, C, D, E, F>,
    arrow.core.Tuple6<A, B, C, D, E, F>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C, D, E, F> Companion.order(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>
): Tuple6Order<A, B, C, D, E, F> = object : arrow.core.extensions.Tuple6Order<A, B, C, D, E, F> {
    override fun OA(): arrow.typeclasses.Order<A> = OA

  override fun OB(): arrow.typeclasses.Order<B> = OB

  override fun OC(): arrow.typeclasses.Order<C> = OC

  override fun OD(): arrow.typeclasses.Order<D> = OD

  override fun OE(): arrow.typeclasses.Order<E> = OE

  override fun OF(): arrow.typeclasses.Order<F> = OF }
