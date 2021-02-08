package arrow.core.extensions.sequence.order

import arrow.core.SequenceK
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKOrder
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation
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
  OrderDeprecation,
  ReplaceWith("this.toList().compareTo(arg1.toList())", "arrow.core.compareTo")
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
  OrderDeprecation,
  ReplaceWith("this.toList() == arg1.toList()")
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
  OrderDeprecation,
  ReplaceWith("this.toList() < arg1.toList()", "arrow.core.compareTo")
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
  OrderDeprecation,
  ReplaceWith("this.toList() <= arg1.toList()", "arrow.core.compareTo")
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
  OrderDeprecation,
  ReplaceWith("this.toList() > arg1.toList()", "arrow.core.compareTo")
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
  OrderDeprecation,
  ReplaceWith("this.toList() >= arg1.toList()", "arrow.core.compareTo")
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
  OrderDeprecation,
  ReplaceWith("maxOf(this.toList(), arg1.toList())")
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
  OrderDeprecation,
  ReplaceWith("minOf(this.toList(), arg1.toList())")
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
  OrderDeprecation,
  ReplaceWith("sort(this.toList(), arg1.toList()).let { (a, b) -> Tuple2(b.asSequence(), a.asSequence()) }", "arrow.core.Tuple2", "arrow.core.sort")
)
fun <A> Sequence<A>.sort(OA: Order<A>, arg1: Sequence<A>): Tuple2<SequenceK<A>, SequenceK<A>> =
    arrow.core.extensions.sequence.order.Sequence.order<A>(OA).run {
  arrow.core.SequenceK(this@sort).sort(arrow.core.SequenceK(arg1)) as
    arrow.core.Tuple2<arrow.core.SequenceK<A>, arrow.core.SequenceK<A>>
}

@Deprecated("Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions")
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(OrderDeprecation)
  inline fun <A> order(OA: Order<A>): SequenceKOrder<A> = object :
      arrow.core.extensions.SequenceKOrder<A> { override fun OA(): arrow.typeclasses.Order<A> = OA
      }}
