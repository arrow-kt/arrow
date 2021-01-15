package arrow.core.extensions.hashed.order

import arrow.core.Hashed
import arrow.core.Hashed.Companion
import arrow.core.Tuple2
import arrow.core.extensions.HashedOrder
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
  "compareTo(ORD, arg1)",
  "arrow.core.compareTo"
  ),
  DeprecationLevel.WARNING
)
fun <A> Hashed<A>.compareTo(ORD: Order<A>, arg1: Hashed<A>): Int =
    arrow.core.Hashed.order<A>(ORD).run {
  this@compareTo.compareTo(arg1) as kotlin.Int
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
  "lt(ORD, arg1)",
  "arrow.core.lt"
  ),
  DeprecationLevel.WARNING
)
fun <A> Hashed<A>.lt(ORD: Order<A>, arg1: Hashed<A>): Boolean =
    arrow.core.Hashed.order<A>(ORD).run {
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
  "lte(ORD, arg1)",
  "arrow.core.lte"
  ),
  DeprecationLevel.WARNING
)
fun <A> Hashed<A>.lte(ORD: Order<A>, arg1: Hashed<A>): Boolean =
    arrow.core.Hashed.order<A>(ORD).run {
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
  "gt(ORD, arg1)",
  "arrow.core.gt"
  ),
  DeprecationLevel.WARNING
)
fun <A> Hashed<A>.gt(ORD: Order<A>, arg1: Hashed<A>): Boolean =
    arrow.core.Hashed.order<A>(ORD).run {
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
  "gte(ORD, arg1)",
  "arrow.core.gte"
  ),
  DeprecationLevel.WARNING
)
fun <A> Hashed<A>.gte(ORD: Order<A>, arg1: Hashed<A>): Boolean =
    arrow.core.Hashed.order<A>(ORD).run {
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
  "max(ORD, arg1)",
  "arrow.core.max"
  ),
  DeprecationLevel.WARNING
)
fun <A> Hashed<A>.max(ORD: Order<A>, arg1: Hashed<A>): Hashed<A> =
    arrow.core.Hashed.order<A>(ORD).run {
  this@max.max(arg1) as arrow.core.Hashed<A>
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
  "min(ORD, arg1)",
  "arrow.core.min"
  ),
  DeprecationLevel.WARNING
)
fun <A> Hashed<A>.min(ORD: Order<A>, arg1: Hashed<A>): Hashed<A> =
    arrow.core.Hashed.order<A>(ORD).run {
  this@min.min(arg1) as arrow.core.Hashed<A>
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
  "sort(ORD, arg1)",
  "arrow.core.sort"
  ),
  DeprecationLevel.WARNING
)
fun <A> Hashed<A>.sort(ORD: Order<A>, arg1: Hashed<A>): Tuple2<Hashed<A>, Hashed<A>> =
    arrow.core.Hashed.order<A>(ORD).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Hashed<A>, arrow.core.Hashed<A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.order(ORD: Order<A>): HashedOrder<A> = object :
    arrow.core.extensions.HashedOrder<A> { override fun ORD(): arrow.typeclasses.Order<A> = ORD }
