package arrow.optics.extensions.sequence.filterIndex

import arrow.core.SequenceK
import arrow.optics.PTraversal
import arrow.optics.extensions.SequenceKFilterIndex

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the exposed function in the instance for Sequence from the companion object of the typeclass instead.",
  ReplaceWith(
    "FilterIndex.sequence<A>().filter(p)",
    "arrow.optics.sequence", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
fun <A> filter(p: Function1<Int, Boolean>): PTraversal<SequenceK<A>, SequenceK<A>, A, A> =
  arrow.optics.extensions.sequence.filterIndex.Sequence
    .filterIndex<A>()
    .filter(p) as arrow.optics.PTraversal<arrow.core.SequenceK<A>, arrow.core.SequenceK<A>, A, A>

/**
 * cached extension
 */
@PublishedApi()
internal val filterIndex_singleton: SequenceKFilterIndex<Any?> = object : SequenceKFilterIndex<Any?>
{}

@Deprecated("Receiver Sequence object is deprecated, and it will be removed in 0.13.")
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Typeclass instance have been moved to the companion object of the typeclass.",
    ReplaceWith(
      "FilterIndex.sequence<A>()",
      "arrow.optics.sequence", "arrow.optics.typeclasses.FilterIndex"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> filterIndex(): SequenceKFilterIndex<A> = filterIndex_singleton as
    arrow.optics.extensions.SequenceKFilterIndex<A>
}
