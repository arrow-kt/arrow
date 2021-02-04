package arrow.core.extensions.ordering.eq

import arrow.core.Ordering
import arrow.core.Ordering.Companion
import arrow.core.extensions.OrderingEq
import arrow.typeclasses.EqDeprecation
import kotlin.Boolean
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val eq_singleton: OrderingEq = object : arrow.core.extensions.OrderingEq {}

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(EqDeprecation, ReplaceWith("this != arg1"))
fun Ordering.neqv(arg1: Ordering): Boolean = arrow.core.Ordering.eq().run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(EqDeprecation)
inline fun Companion.eq(): OrderingEq = eq_singleton
