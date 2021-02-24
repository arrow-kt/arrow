package arrow.core.extensions.ordering.monoid

import arrow.core.Ordering
import arrow.core.Ordering.Companion
import arrow.core.extensions.OrderingMonoid
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monoid_singleton: OrderingMonoid = object : arrow.core.extensions.OrderingMonoid {}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("combineAll()", "arrow.core.combineAll"))
fun Collection<Ordering>.combineAll(): Ordering = arrow.core.Ordering.monoid().run {
  this@combineAll.combineAll() as arrow.core.Ordering
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("arg0.combineAll()", "arrow.core.combineAll"))
fun combineAll(arg0: List<Ordering>): Ordering = arrow.core.Ordering
  .monoid()
  .combineAll(arg0) as arrow.core.Ordering

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Monoid.ordering()", "arrow.core.Monoid", "arrow.core.ordering"))
inline fun Companion.monoid(): OrderingMonoid = monoid_singleton
