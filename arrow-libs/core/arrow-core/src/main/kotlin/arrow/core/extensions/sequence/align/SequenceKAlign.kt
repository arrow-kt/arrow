package arrow.core.extensions.sequence.align

import arrow.core.extensions.SequenceKAlign
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
    "emptySequence<A>()"
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

@Deprecated(
  "Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions",
  level = DeprecationLevel.WARNING
)
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Align typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun align(): SequenceKAlign = align_singleton}
