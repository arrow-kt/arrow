package arrow.core.extensions.sequence.semigroup

import arrow.core.extensions.SequenceKSemigroup
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName
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
  "plus(arg1)",
  "arrow.core.plus"
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
  "maybeCombine(arg1)",
  "arrow.core.maybeCombine"
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

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> semigroup(): SequenceKSemigroup<A> = semigroup_singleton as
      arrow.core.extensions.SequenceKSemigroup<A>}
