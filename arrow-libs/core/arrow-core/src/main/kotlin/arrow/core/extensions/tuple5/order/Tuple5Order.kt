package arrow.core.extensions.tuple5.order

import arrow.core.Tuple2
import arrow.core.Tuple5
import arrow.core.Tuple5.Companion
import arrow.core.extensions.Tuple5Order
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
  "compareTo(OA, OB, OC, OD, OE, arg1)",
  "arrow.core.compareTo"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.compareTo(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  arg1: Tuple5<A, B, C, D, E>
): Int = arrow.core.Tuple5.order<A, B, C, D, E>(OA, OB, OC, OD, OE).run {
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
  "eqv(OA, OB, OC, OD, OE, arg1)",
  "arrow.core.eqv"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.eqv(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  arg1: Tuple5<A, B, C, D, E>
): Boolean = arrow.core.Tuple5.order<A, B, C, D, E>(OA, OB, OC, OD, OE).run {
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
  "lt(OA, OB, OC, OD, OE, arg1)",
  "arrow.core.lt"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.lt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  arg1: Tuple5<A, B, C, D, E>
): Boolean = arrow.core.Tuple5.order<A, B, C, D, E>(OA, OB, OC, OD, OE).run {
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
  "lte(OA, OB, OC, OD, OE, arg1)",
  "arrow.core.lte"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.lte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  arg1: Tuple5<A, B, C, D, E>
): Boolean = arrow.core.Tuple5.order<A, B, C, D, E>(OA, OB, OC, OD, OE).run {
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
  "gt(OA, OB, OC, OD, OE, arg1)",
  "arrow.core.gt"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.gt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  arg1: Tuple5<A, B, C, D, E>
): Boolean = arrow.core.Tuple5.order<A, B, C, D, E>(OA, OB, OC, OD, OE).run {
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
  "gte(OA, OB, OC, OD, OE, arg1)",
  "arrow.core.gte"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.gte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  arg1: Tuple5<A, B, C, D, E>
): Boolean = arrow.core.Tuple5.order<A, B, C, D, E>(OA, OB, OC, OD, OE).run {
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
  "max(OA, OB, OC, OD, OE, arg1)",
  "arrow.core.max"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.max(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  arg1: Tuple5<A, B, C, D, E>
): Tuple5<A, B, C, D, E> = arrow.core.Tuple5.order<A, B, C, D, E>(OA, OB, OC, OD, OE).run {
  this@max.max(arg1) as arrow.core.Tuple5<A, B, C, D, E>
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
  "min(OA, OB, OC, OD, OE, arg1)",
  "arrow.core.min"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.min(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  arg1: Tuple5<A, B, C, D, E>
): Tuple5<A, B, C, D, E> = arrow.core.Tuple5.order<A, B, C, D, E>(OA, OB, OC, OD, OE).run {
  this@min.min(arg1) as arrow.core.Tuple5<A, B, C, D, E>
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
  "sort(OA, OB, OC, OD, OE, arg1)",
  "arrow.core.sort"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.sort(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  arg1: Tuple5<A, B, C, D, E>
): Tuple2<Tuple5<A, B, C, D, E>, Tuple5<A, B, C, D, E>> = arrow.core.Tuple5.order<A, B, C, D,
    E>(OA, OB, OC, OD, OE).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Tuple5<A, B, C, D, E>, arrow.core.Tuple5<A,
    B, C, D, E>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C, D, E> Companion.order(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>
): Tuple5Order<A, B, C, D, E> = object : arrow.core.extensions.Tuple5Order<A, B, C, D, E> { override
    fun OA(): arrow.typeclasses.Order<A> = OA

  override fun OB(): arrow.typeclasses.Order<B> = OB

  override fun OC(): arrow.typeclasses.Order<C> = OC

  override fun OD(): arrow.typeclasses.Order<D> = OD

  override fun OE(): arrow.typeclasses.Order<E> = OE }
