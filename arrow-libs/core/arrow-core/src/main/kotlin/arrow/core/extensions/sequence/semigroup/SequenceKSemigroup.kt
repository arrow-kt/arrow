package arrow.core.extensions.sequence.semigroup

import arrow.core.extensions.SequenceKSemigroup
import kotlin.sequences.Sequence

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this + arg1"
  ),
  DeprecationLevel.WARNING
)
operator fun <A> Sequence<A>.plus(arg1: Sequence<A>): Sequence<A> =
    arrow.core.extensions.sequence.semigroup.Sequence.semigroup<A>().run {
  arrow.core.SequenceK(this@plus).plus(arrow.core.SequenceK(arg1)) as kotlin.sequences.Sequence<A>
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "(arg1?.plus(this) ?: emptySequence<A>())"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.maybeCombine(arg1: Sequence<A>): Sequence<A> =
    arrow.core.extensions.sequence.semigroup.Sequence.semigroup<A>().run {
  arrow.core.SequenceK(this@maybeCombine).maybeCombine(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val semigroup_singleton: SequenceKSemigroup<Any?> = object : SequenceKSemigroup<Any?> {}

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
    "@extension projected functions are deprecated",
    ReplaceWith(
      "Semigroup.sequence<A>()",
      "arrow.core.sequence", "arrow.typeclasses.Semigroup"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> semigroup(): SequenceKSemigroup<A> = semigroup_singleton as
      arrow.core.extensions.SequenceKSemigroup<A>}
