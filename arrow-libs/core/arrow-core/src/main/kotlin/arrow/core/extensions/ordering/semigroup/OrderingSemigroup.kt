package arrow.core.extensions.ordering.semigroup

import arrow.core.Ordering
import arrow.core.Ordering.Companion
import arrow.core.extensions.OrderingSemigroup
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semigroup_singleton: OrderingSemigroup = object :
    arrow.core.extensions.OrderingSemigroup {}

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("combine(arg1)"))
operator fun Ordering.plus(arg1: Ordering): Ordering = arrow.core.Ordering.semigroup().run {
  this@plus.plus(arg1) as arrow.core.Ordering
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("arg1?.plus(this)"))
fun Ordering.maybeCombine(arg1: Ordering): Ordering = arrow.core.Ordering.semigroup().run {
  this@maybeCombine.maybeCombine(arg1) as arrow.core.Ordering
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Ordering.semigroup()"))
inline fun Companion.semigroup(): OrderingSemigroup = semigroup_singleton
