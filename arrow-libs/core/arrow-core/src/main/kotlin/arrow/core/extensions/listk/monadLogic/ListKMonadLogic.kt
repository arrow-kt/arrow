package arrow.core.extensions.listk.monadLogic

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.ListKMonadLogic
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadLogic_singleton: ListKMonadLogic = object : arrow.core.extensions.ListKMonadLogic
{}

@JvmName("splitM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("split()", "arrow.core.split()"))
fun <A> Kind<ForListK, A>.splitM(): ListK<Option<Tuple2<Kind<ForListK, A>, A>>> =
  arrow.core.ListK.monadLogic().run {
    this@splitM.splitM<A>() as
      arrow.core.ListK<arrow.core.Option<arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>, A>>>
  }

@JvmName("interleave")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("interleave(arg1)", "arrow.core.interleave"))
fun <A> Kind<ForListK, A>.interleave(arg1: Kind<ForListK, A>): ListK<A> =
  arrow.core.ListK.monadLogic().run {
    this@interleave.interleave<A>(arg1) as arrow.core.ListK<A>
  }

@JvmName("unweave")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("unweave(arg1)", "arrow.core.unweave"))
fun <A, B> Kind<ForListK, A>.unweave(arg1: Function1<A, Kind<ForListK, B>>): ListK<B> =
  arrow.core.ListK.monadLogic().run {
    this@unweave.unweave<A, B>(arg1) as arrow.core.ListK<B>
  }

@JvmName("ifThen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("ifThen(arg1, arg2)", "arrow.core.ifThen"))
fun <A, B> Kind<ForListK, A>.ifThen(arg1: Kind<ForListK, B>, arg2: Function1<A, Kind<ForListK, B>>):
  ListK<B> = arrow.core.ListK.monadLogic().run {
    this@ifThen.ifThen<A, B>(arg1, arg2) as arrow.core.ListK<B>
  }

@JvmName("once")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("listOfNotNull<A>(firstOrNull())"))
fun <A> Kind<ForListK, A>.once(): ListK<A> = arrow.core.ListK.monadLogic().run {
  this@once.once<A>() as arrow.core.ListK<A>
}

@JvmName("voidIfValue")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("firstOrNull()?.let { emptyList<Unit>() } ?: listOf(Unit)"))
fun <A> Kind<ForListK, A>.voidIfValue(): ListK<Unit> = arrow.core.ListK.monadLogic().run {
  this@voidIfValue.voidIfValue<A>() as arrow.core.ListK<kotlin.Unit>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("MonadLogic typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.monadLogic(): ListKMonadLogic = monadLogic_singleton
