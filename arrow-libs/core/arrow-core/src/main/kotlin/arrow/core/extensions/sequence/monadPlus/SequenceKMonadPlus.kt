package arrow.core.extensions.sequence.monadPlus

import arrow.core.extensions.SequenceKMonadPlus
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName
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
  "zeroM()",
  "arrow.core.extensions.sequence.monadPlus.Sequence.zeroM"
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
  "plusM(arg1)",
  "arrow.core.plusM"
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

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monadPlus(): SequenceKMonadPlus = monadPlus_singleton}
