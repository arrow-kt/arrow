package arrow.core.extensions.sequencek.monadLogic

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKMonadLogic
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadLogic_singleton: SequenceKMonadLogic = object :
    arrow.core.extensions.SequenceKMonadLogic {}

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
  "splitM()",
  "arrow.core.splitM"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForSequenceK, A>.splitM(): SequenceK<Option<Tuple2<Kind<ForSequenceK, A>, A>>> =
    arrow.core.SequenceK.monadLogic().run {
  this@splitM.splitM<A>() as
    arrow.core.SequenceK<arrow.core.Option<arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK, A>,
    A>>>
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
  "interleave(arg1)",
  "arrow.core.interleave"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForSequenceK, A>.interleave(arg1: Kind<ForSequenceK, A>): SequenceK<A> =
    arrow.core.SequenceK.monadLogic().run {
  this@interleave.interleave<A>(arg1) as arrow.core.SequenceK<A>
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
  "unweave(arg1)",
  "arrow.core.unweave"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.unweave(arg1: Function1<A, Kind<ForSequenceK, B>>): SequenceK<B> =
    arrow.core.SequenceK.monadLogic().run {
  this@unweave.unweave<A, B>(arg1) as arrow.core.SequenceK<B>
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
  "ifThen(arg1, arg2)",
  "arrow.core.ifThen"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, A>.ifThen(
  arg1: Kind<ForSequenceK, B>,
  arg2: Function1<A, Kind<ForSequenceK, B>>
): SequenceK<B> = arrow.core.SequenceK.monadLogic().run {
  this@ifThen.ifThen<A, B>(arg1, arg2) as arrow.core.SequenceK<B>
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
  "once()",
  "arrow.core.once"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForSequenceK, A>.once(): SequenceK<A> = arrow.core.SequenceK.monadLogic().run {
  this@once.once<A>() as arrow.core.SequenceK<A>
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
  "voidIfValue()",
  "arrow.core.voidIfValue"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForSequenceK, A>.voidIfValue(): SequenceK<Unit> =
    arrow.core.SequenceK.monadLogic().run {
  this@voidIfValue.voidIfValue<A>() as arrow.core.SequenceK<kotlin.Unit>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.monadLogic(): SequenceKMonadLogic = monadLogic_singleton
