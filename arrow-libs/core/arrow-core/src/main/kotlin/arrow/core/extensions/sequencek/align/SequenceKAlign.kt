package arrow.core.extensions.sequencek.align

import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKAlign

/**
 * cached extension
 */
@PublishedApi()
internal val align_singleton: SequenceKAlign = object : arrow.core.extensions.SequenceKAlign {}

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
fun <A> empty(): SequenceK<A> = arrow.core.SequenceK
  .align()
  .empty<A>() as arrow.core.SequenceK<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Align typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
inline fun Companion.align(): SequenceKAlign = align_singleton
