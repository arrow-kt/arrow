package arrow.core.extensions.sequence.monadPlus

import arrow.core.extensions.SequenceKMonadPlus
import kotlin.sequences.Sequence

@JvmName("zeroM")
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
fun <A> zeroM(): Sequence<A> = arrow.core.extensions.sequence.monadPlus.Sequence
   .monadPlus()
   .zeroM<A>() as kotlin.sequences.Sequence<A>

@JvmName("plusM")
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
fun <A> Sequence<A>.plusM(arg1: Sequence<A>): Sequence<A> =
    arrow.core.extensions.sequence.monadPlus.Sequence.monadPlus().run {
  arrow.core.SequenceK(this@plusM).plusM<A>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val monadPlus_singleton: SequenceKMonadPlus = object :
    arrow.core.extensions.SequenceKMonadPlus {}

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
    "MonadPlus typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun monadPlus(): SequenceKMonadPlus = monadPlus_singleton}
