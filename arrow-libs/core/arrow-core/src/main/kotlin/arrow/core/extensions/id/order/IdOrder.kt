package arrow.core.extensions.id.order

import arrow.core.Id
import arrow.core.Id.Companion
import arrow.core.Tuple2
import arrow.core.extensions.IdOrder
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
  "compareTo(OA, arg1)",
  "arrow.core.compareTo"
  ),
  DeprecationLevel.WARNING
)
fun <A> Id<A>.compareTo(OA: Order<A>, arg1: Id<A>): Int = arrow.core.Id.order<A>(OA).run {
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
  "eqv(OA, arg1)",
  "arrow.core.eqv"
  ),
  DeprecationLevel.WARNING
)
fun <A> Id<A>.eqv(OA: Order<A>, arg1: Id<A>): Boolean = arrow.core.Id.order<A>(OA).run {
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
  "lt(OA, arg1)",
  "arrow.core.lt"
  ),
  DeprecationLevel.WARNING
)
fun <A> Id<A>.lt(OA: Order<A>, arg1: Id<A>): Boolean = arrow.core.Id.order<A>(OA).run {
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
  "lte(OA, arg1)",
  "arrow.core.lte"
  ),
  DeprecationLevel.WARNING
)
fun <A> Id<A>.lte(OA: Order<A>, arg1: Id<A>): Boolean = arrow.core.Id.order<A>(OA).run {
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
  "gt(OA, arg1)",
  "arrow.core.gt"
  ),
  DeprecationLevel.WARNING
)
fun <A> Id<A>.gt(OA: Order<A>, arg1: Id<A>): Boolean = arrow.core.Id.order<A>(OA).run {
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
  "gte(OA, arg1)",
  "arrow.core.gte"
  ),
  DeprecationLevel.WARNING
)
fun <A> Id<A>.gte(OA: Order<A>, arg1: Id<A>): Boolean = arrow.core.Id.order<A>(OA).run {
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
  "max(OA, arg1)",
  "arrow.core.max"
  ),
  DeprecationLevel.WARNING
)
fun <A> Id<A>.max(OA: Order<A>, arg1: Id<A>): Id<A> = arrow.core.Id.order<A>(OA).run {
  this@max.max(arg1) as arrow.core.Id<A>
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
fun <A> Id<A>.min(OA: Order<A>, arg1: Id<A>): Id<A> = arrow.core.Id.order<A>(OA).run {
  this@min.min(arg1) as arrow.core.Id<A>
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
fun <A> Id<A>.sort(OA: Order<A>, arg1: Id<A>): Tuple2<Id<A>, Id<A>> =
    arrow.core.Id.order<A>(OA).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Id<A>, arrow.core.Id<A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.order(OA: Order<A>): IdOrder<A> = object : arrow.core.extensions.IdOrder<A>
    { override fun OA(): arrow.typeclasses.Order<A> = OA }
