package arrow.core.extensions.sequencek.repeat

import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKRepeat

/**
 * cached extension
 */
@PublishedApi()
internal val repeat_singleton: SequenceKRepeat = object : arrow.core.extensions.SequenceKRepeat {}

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
fun <A> repeat(a: A): SequenceK<A> = arrow.core.SequenceK
   .repeat()
   .repeat<A>(a) as arrow.core.SequenceK<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Repeat typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
inline fun Companion.repeat(): SequenceKRepeat = repeat_singleton
