package arrow.core.extensions.sequence.repeat

import arrow.core.extensions.SequenceKRepeat
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName
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
  "repeat(a)",
  "arrow.core.extensions.sequence.repeat.Sequence.repeat"
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

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun repeat(): SequenceKRepeat = repeat_singleton}
