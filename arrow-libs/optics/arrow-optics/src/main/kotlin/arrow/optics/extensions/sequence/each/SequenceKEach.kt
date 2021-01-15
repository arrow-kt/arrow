package arrow.optics.extensions.sequence.each

import arrow.core.SequenceK
import arrow.optics.PTraversal
import arrow.optics.extensions.SequenceKEach

@JvmName("each")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Each is being deprecated. Use the instance for Sequence from Traversal's companion object instead.",
  ReplaceWith(
    "Traversal.sequence<A>()",
    "arrow.optics.Traversal", "arrow.optics.sequence"),
  DeprecationLevel.WARNING
)
fun <A> each(): PTraversal<SequenceK<A>, SequenceK<A>, A, A> =
    arrow.optics.extensions.sequence.each.Sequence
   .each<A>()
   .each() as arrow.optics.PTraversal<arrow.core.SequenceK<A>, arrow.core.SequenceK<A>, A, A>

/**
 * cached extension
 */
@PublishedApi()
internal val each_singleton: SequenceKEach<Any?> = object : SequenceKEach<Any?> {}

@Deprecated("Receiver Sequence object is deprecated, and it will be removed in 0.13.")
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Each is being deprecated. Use the instance for Sequence from Traversal's companion object instead.",
    ReplaceWith(
      "Traversal.sequence<A>()",
      "arrow.optics.Traversal", "arrow.optics.sequence"),
    DeprecationLevel.WARNING
  )
  inline fun <A> each(): SequenceKEach<A> = each_singleton as
      arrow.optics.extensions.SequenceKEach<A>}
