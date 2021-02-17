package arrow.core.extensions.list.monadLogic

import arrow.Kind
import arrow.core.ForListK
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.ListKMonadLogic
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("splitM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("split()", "arrow.core.split"))
fun <A> List<A>.splitM(): List<Option<Tuple2<Kind<ForListK, A>, A>>> =
  arrow.core.extensions.list.monadLogic.List.monadLogic().run {
    arrow.core.ListK(this@splitM).splitM<A>() as
      kotlin.collections.List<arrow.core.Option<arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>,
            A>>>
  }

@JvmName("interleave")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("interleave(arg1)", "arrow.core.interleave"))
fun <A> List<A>.interleave(arg1: List<A>): List<A> =
  arrow.core.extensions.list.monadLogic.List.monadLogic().run {
    arrow.core.ListK(this@interleave).interleave<A>(arrow.core.ListK(arg1)) as
      kotlin.collections.List<A>
  }

@JvmName("unweave")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("unweave(arg1)", "arrow.core.unweave"))
fun <A, B> List<A>.unweave(arg1: Function1<A, Kind<ForListK, B>>): List<B> =
  arrow.core.extensions.list.monadLogic.List.monadLogic().run {
    arrow.core.ListK(this@unweave).unweave<A, B>(arg1) as kotlin.collections.List<B>
  }

@JvmName("ifThen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("ifThen(arg1, arg2)", "arrow.core.ifThen"))
fun <A, B> List<A>.ifThen(arg1: List<B>, arg2: Function1<A, Kind<ForListK, B>>): List<B> =
  arrow.core.extensions.list.monadLogic.List.monadLogic().run {
    arrow.core.ListK(this@ifThen).ifThen<A, B>(arrow.core.ListK(arg1), arg2) as
      kotlin.collections.List<B>
  }

@JvmName("once")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("listOfNotNull<A>(firstOrNull())"))
fun <A> List<A>.once(): List<A> = arrow.core.extensions.list.monadLogic.List.monadLogic().run {
  arrow.core.ListK(this@once).once<A>() as kotlin.collections.List<A>
}

@JvmName("voidIfValue")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("firstOrNull()?.let { emptyList<Unit>() } ?: listOf(Unit)"))
fun <A> List<A>.voidIfValue(): List<Unit> =
  arrow.core.extensions.list.monadLogic.List.monadLogic().run {
    arrow.core.ListK(this@voidIfValue).voidIfValue<A>() as kotlin.collections.List<kotlin.Unit>
  }

/**
 * cached extension
 */
@PublishedApi()
internal val monadLogic_singleton: ListKMonadLogic = object : arrow.core.extensions.ListKMonadLogic
{}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("MonadLogic typeclasses is deprecated. Use concrete methods on List")
  inline fun monadLogic(): ListKMonadLogic = monadLogic_singleton
}
