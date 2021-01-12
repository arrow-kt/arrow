package arrow.core.extensions.ordering.show

import arrow.core.Ordering.Companion
import arrow.core.extensions.OrderingShow
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val show_singleton: OrderingShow = object : arrow.core.extensions.OrderingShow {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("Show.ordering()", "arrow.core.Show", "arrow.core.ordering"))
inline fun Companion.show(): OrderingShow = show_singleton
