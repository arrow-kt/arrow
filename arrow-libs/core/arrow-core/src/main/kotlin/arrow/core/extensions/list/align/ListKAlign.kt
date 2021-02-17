package arrow.core.extensions.list.align

import arrow.core.extensions.ListKAlign
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("empty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("emptyList()"))
fun <A> empty(): List<A> =
  emptyList()

/**
 * cached extension
 */
@PublishedApi()
internal val align_singleton: ListKAlign = object : arrow.core.extensions.ListKAlign {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Align typeclasses is deprecated. Use concrete methods on List")
  inline fun align(): ListKAlign = align_singleton
}
