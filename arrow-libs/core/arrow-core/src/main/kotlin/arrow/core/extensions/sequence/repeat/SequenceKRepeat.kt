package arrow.core.extensions.sequence.repeat

import arrow.core.extensions.SequenceKRepeat
import kotlin.sequences.Sequence

@JvmName("repeat")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "generateSequence { a }"
  ),
  DeprecationLevel.WARNING
)
fun <A> repeat(a: A): Sequence<A> = arrow.core.extensions.sequence.repeat.Sequence
   .repeat()
   .repeat<A>(a) as kotlin.sequences.Sequence<A>

/**
 * cached extension
 */
@PublishedApi()
internal val repeat_singleton: SequenceKRepeat = object : arrow.core.extensions.SequenceKRepeat {}

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
    "Repeat typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun repeat(): SequenceKRepeat = repeat_singleton}
