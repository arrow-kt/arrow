package arrow.core.extensions.sequence.order

import arrow.core.SequenceK
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKOrder
import arrow.typeclasses.Order
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Int
import kotlin.Suppress
import kotlin.jvm.JvmName
import kotlin.sequences.Sequence

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
fun <A> Sequence<A>.compareTo(OA: Order<A>, arg1: Sequence<A>): Int =
    arrow.core.extensions.sequence.order.Sequence.order<A>(OA).run {
  arrow.core.SequenceK(this@compareTo).compareTo(arrow.core.SequenceK(arg1)) as kotlin.Int
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
fun <A> Sequence<A>.eqv(OA: Order<A>, arg1: Sequence<A>): Boolean =
    arrow.core.extensions.sequence.order.Sequence.order<A>(OA).run {
  arrow.core.SequenceK(this@eqv).eqv(arrow.core.SequenceK(arg1)) as kotlin.Boolean
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
fun <A> Sequence<A>.lt(OA: Order<A>, arg1: Sequence<A>): Boolean =
    arrow.core.extensions.sequence.order.Sequence.order<A>(OA).run {
  arrow.core.SequenceK(this@lt).lt(arrow.core.SequenceK(arg1)) as kotlin.Boolean
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
fun <A> Sequence<A>.lte(OA: Order<A>, arg1: Sequence<A>): Boolean =
    arrow.core.extensions.sequence.order.Sequence.order<A>(OA).run {
  arrow.core.SequenceK(this@lte).lte(arrow.core.SequenceK(arg1)) as kotlin.Boolean
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
fun <A> Sequence<A>.gt(OA: Order<A>, arg1: Sequence<A>): Boolean =
    arrow.core.extensions.sequence.order.Sequence.order<A>(OA).run {
  arrow.core.SequenceK(this@gt).gt(arrow.core.SequenceK(arg1)) as kotlin.Boolean
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
fun <A> Sequence<A>.gte(OA: Order<A>, arg1: Sequence<A>): Boolean =
    arrow.core.extensions.sequence.order.Sequence.order<A>(OA).run {
  arrow.core.SequenceK(this@gte).gte(arrow.core.SequenceK(arg1)) as kotlin.Boolean
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
fun <A> Sequence<A>.max(OA: Order<A>, arg1: Sequence<A>): Sequence<A> =
    arrow.core.extensions.sequence.order.Sequence.order<A>(OA).run {
  arrow.core.SequenceK(this@max).max(arrow.core.SequenceK(arg1)) as kotlin.sequences.Sequence<A>
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
fun <A> Sequence<A>.min(OA: Order<A>, arg1: Sequence<A>): Sequence<A> =
    arrow.core.extensions.sequence.order.Sequence.order<A>(OA).run {
  arrow.core.SequenceK(this@min).min(arrow.core.SequenceK(arg1)) as kotlin.sequences.Sequence<A>
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
fun <A> Sequence<A>.sort(OA: Order<A>, arg1: Sequence<A>): Tuple2<SequenceK<A>, SequenceK<A>> =
    arrow.core.extensions.sequence.order.Sequence.order<A>(OA).run {
  arrow.core.SequenceK(this@sort).sort(arrow.core.SequenceK(arg1)) as
    arrow.core.Tuple2<arrow.core.SequenceK<A>, arrow.core.SequenceK<A>>
}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> order(OA: Order<A>): SequenceKOrder<A> = object :
      arrow.core.extensions.SequenceKOrder<A> { override fun OA(): arrow.typeclasses.Order<A> = OA
      }}
