package arrow.core.extensions.listk.align

import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKAlign
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val align_singleton: ListKAlign = object : arrow.core.extensions.ListKAlign {}

@JvmName("empty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("emptyList()"))
fun <A> empty(): ListK<A> = arrow.core.ListK
  .align()
  .empty<A>() as arrow.core.ListK<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Align typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.align(): ListKAlign = align_singleton
