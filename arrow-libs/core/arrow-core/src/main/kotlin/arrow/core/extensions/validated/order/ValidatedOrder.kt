package arrow.core.extensions.validated.order

import arrow.core.Tuple2
import arrow.core.Validated
import arrow.core.Validated.Companion
import arrow.core.compareTo
import arrow.core.lt
import arrow.core.lte
import arrow.core.gt
import arrow.core.gte
import arrow.core.sort
import arrow.core.min
import arrow.core.max
import arrow.core.extensions.ValidatedOrder
import arrow.typeclasses.Order
import kotlin.Boolean
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
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("compareTo(OL, OR, arg1)", "arrow.core.compareTo"))
fun <L, R> Validated<L, R>.compareTo(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Int = compareTo(OL, OR, arg1)

@JvmName("eqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("eqv(OL, OR, arg1)", "arrow.core.eqv"))
fun <L, R> Validated<L, R>.eqv(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Boolean = eqv(OL, OR, arg1)

@JvmName("lt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("lt(OL, OR, arg1)", "arrow.core.lt"))
fun <L, R> Validated<L, R>.lt(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Boolean = lt(OL, OR, arg1)

@JvmName("lte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("lte(OL, OR, arg1)", "arrow.core.lte"))
fun <L, R> Validated<L, R>.lte(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Boolean = lte(OL, OR, arg1)

@JvmName("gt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("gt(OL, OR, arg1)", "arrow.core.gt"))
fun <L, R> Validated<L, R>.gt(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Boolean = gt(OL, OR, arg1)

@JvmName("gte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("gte(OL, OR, arg1)", "arrow.core.gte"))
fun <L, R> Validated<L, R>.gte(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Boolean = gte(OL, OR, arg1)

@JvmName("max")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("max(OL, OR, arg1)", "arrow.core.max"))
fun <L, R> Validated<L, R>.max(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Validated<L, R> = max(OL, OR, arg1)

@JvmName("min")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("min(OL, OR, arg1)", "arrow.core.min"))
fun <L, R> Validated<L, R>.min(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Validated<L, R> = min(OL, OR, arg1)

@JvmName("sort")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("sort(OL, OR, arg1)", "arrow.core.sort"))
fun <L, R> Validated<L, R>.sort(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Validated<L, R>
): Tuple2<Validated<L, R>, Validated<L, R>> =
  sort(OL, OR, arg1)

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Order.validated(OL, OR)", "arrow.core.Order", "arrow.core.validated"))
inline fun <L, R> Companion.order(OL: Order<L>, OR: Order<R>): ValidatedOrder<L, R> = object :
  arrow.core.extensions.ValidatedOrder<L, R> {
  override fun OL(): arrow.typeclasses.Order<L> = OL

  override fun OR(): arrow.typeclasses.Order<R> = OR
}
