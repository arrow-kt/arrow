package arrow.core.extensions.sequencek.order

import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKOrder
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
  ReplaceWith("this.toList().compareTo(arg1.toList())", "arrow.core.compareTo")
)
fun <A> SequenceK<A>.compareTo(OA: Order<A>, arg1: SequenceK<A>): Int =
  arrow.core.SequenceK.order<A>(OA).run {
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
  ReplaceWith("this.toList() == arg1.toList()")
)
fun <A> SequenceK<A>.eqv(OA: Order<A>, arg1: SequenceK<A>): Boolean =
  arrow.core.SequenceK.order<A>(OA).run {
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
  ReplaceWith("this.toList() < arg1.toList()", "arrow.core.compareTo")
)
fun <A> SequenceK<A>.lt(OA: Order<A>, arg1: SequenceK<A>): Boolean =
  arrow.core.SequenceK.order<A>(OA).run {
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
  ReplaceWith("this.toList() <= arg1.toList()", "arrow.core.compareTo")
)
fun <A> SequenceK<A>.lte(OA: Order<A>, arg1: SequenceK<A>): Boolean =
  arrow.core.SequenceK.order<A>(OA).run {
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
  ReplaceWith("this.toList() > arg1.toList()", "arrow.core.compareTo")
)
fun <A> SequenceK<A>.gt(OA: Order<A>, arg1: SequenceK<A>): Boolean =
  arrow.core.SequenceK.order<A>(OA).run {
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
  ReplaceWith("this.toList() >= arg1.toList()", "arrow.core.compareTo")
)
fun <A> SequenceK<A>.gte(OA: Order<A>, arg1: SequenceK<A>): Boolean =
  arrow.core.SequenceK.order<A>(OA).run {
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
  ReplaceWith("maxOf(this.toList(), arg1.toList())")
)
fun <A> SequenceK<A>.max(OA: Order<A>, arg1: SequenceK<A>): SequenceK<A> =
  arrow.core.SequenceK.order<A>(OA).run {
    this@max.max(arg1) as arrow.core.SequenceK<A>
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
fun <A> SequenceK<A>.min(OA: Order<A>, arg1: SequenceK<A>): SequenceK<A> =
  arrow.core.SequenceK.order<A>(OA).run {
    this@min.min(arg1) as arrow.core.SequenceK<A>
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
fun <A> SequenceK<A>.sort(OA: Order<A>, arg1: SequenceK<A>): Tuple2<SequenceK<A>, SequenceK<A>> =
  arrow.core.SequenceK.order<A>(OA).run {
    this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.SequenceK<A>, arrow.core.SequenceK<A>>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(OrderDeprecation)
inline fun <A> Companion.order(OA: Order<A>): SequenceKOrder<A> = object :
  arrow.core.extensions.SequenceKOrder<A> { override fun OA(): arrow.typeclasses.Order<A> = OA }
