package arrow.core.extensions.sequence.align

import arrow.core.extensions.SequenceKAlign
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName
import kotlin.sequences.Sequence

@JvmName("empty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "empty()",
  "arrow.core.extensions.sequence.align.Sequence.empty"
  ),
  DeprecationLevel.WARNING
)
fun <A> empty(): Sequence<A> =
  arrow.core.extensions.sequence.align.Sequence
    .align()
    .empty<A>() as kotlin.sequences.Sequence<A>

/**
 * cached extension
 */
@PublishedApi()
internal val align_singleton: SequenceKAlign = object : arrow.core.extensions.SequenceKAlign {}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun align(): SequenceKAlign = align_singleton}
