package arrow.core.extensions.sequence.monadLogic

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKMonadLogic
import kotlin.sequences.Sequence

@JvmName("splitM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.split()",
    "arrow.core.split"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.splitM(): Sequence<Option<Tuple2<Kind<ForSequenceK, A>, A>>> =
    arrow.core.extensions.sequence.monadLogic.Sequence.monadLogic().run {
  arrow.core.SequenceK(this@splitM).splitM<A>() as
    kotlin.sequences.Sequence<arrow.core.Option<arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK,
    A>, A>>>
}

@JvmName("interleave")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.interleave(arg1)",
    "arrow.core.interleave"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.interleave(arg1: Sequence<A>): Sequence<A> =
    arrow.core.extensions.sequence.monadLogic.Sequence.monadLogic().run {
  arrow.core.SequenceK(this@interleave).interleave<A>(arrow.core.SequenceK(arg1)) as
    kotlin.sequences.Sequence<A>
}

@JvmName("unweave")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.unweave(arg1)",
    "arrow.core.unweave"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.unweave(arg1: Function1<A, Kind<ForSequenceK, B>>): Sequence<B> =
    arrow.core.extensions.sequence.monadLogic.Sequence.monadLogic().run {
  arrow.core.SequenceK(this@unweave).unweave<A, B>(arg1) as kotlin.sequences.Sequence<B>
}

@JvmName("ifThen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.ifThen(arg1, arg2)",
    "arrow.core.ifThen"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.ifThen(arg1: Sequence<B>, arg2: Function1<A, Kind<ForSequenceK, B>>):
    Sequence<B> = arrow.core.extensions.sequence.monadLogic.Sequence.monadLogic().run {
  arrow.core.SequenceK(this@ifThen).ifThen<A, B>(arrow.core.SequenceK(arg1), arg2) as
    kotlin.sequences.Sequence<B>
}

@JvmName("once")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.once()",
    "arrow.core.once"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.once(): Sequence<A> =
    arrow.core.extensions.sequence.monadLogic.Sequence.monadLogic().run {
  arrow.core.SequenceK(this@once).once<A>() as kotlin.sequences.Sequence<A>
}

@JvmName("voidIfValue")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.firstOrNull()?.let { emptySequence() } ?: sequenceOf(Unit)"
  ),
  DeprecationLevel.WARNING
)
fun <A> Sequence<A>.voidIfValue(): Sequence<Unit> =
    arrow.core.extensions.sequence.monadLogic.Sequence.monadLogic().run {
  arrow.core.SequenceK(this@voidIfValue).voidIfValue<A>() as kotlin.sequences.Sequence<kotlin.Unit>
}

/**
 * cached extension
 */
@PublishedApi()
internal val monadLogic_singleton: SequenceKMonadLogic = object :
    arrow.core.extensions.SequenceKMonadLogic {}

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
    "MonadLogic typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun monadLogic(): SequenceKMonadLogic = monadLogic_singleton}
