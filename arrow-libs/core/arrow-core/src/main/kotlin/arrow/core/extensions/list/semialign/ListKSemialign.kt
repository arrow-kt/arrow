package arrow.core.extensions.list.semialign

import arrow.core.Ior
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.ListKSemialign
import arrow.typeclasses.Semigroup
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("align")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.align(arg1)", "arrow.core.align"))
fun <A, B> align(arg0: List<A>, arg1: List<B>): List<Ior<A, B>> =
    arrow.core.extensions.list.semialign.List
   .semialign()
   .align<A, B>(arrow.core.ListK(arg0), arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Ior<A, B>>

@JvmName("alignWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.align(arg1, arg2)", "arrow.core.align"))
fun <A, B, C> alignWith(
  arg0: List<A>,
  arg1: List<B>,
  arg2: Function1<Ior<A, B>, C>
): List<C> = arrow.core.extensions.list.semialign.List
   .semialign()
   .alignWith<A, B, C>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arg2) as
    kotlin.collections.List<C>

@JvmName("salign")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.salign(arg1, arg2)", "arrow.core.salign"))
fun <A> List<A>.salign(arg1: Semigroup<A>, arg2: List<A>): List<A> =
    arrow.core.extensions.list.semialign.List.semialign().run {
  arrow.core.ListK(this@salign).salign<A>(arg1, arrow.core.ListK(arg2)) as
    kotlin.collections.List<A>
}

@JvmName("padZip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.padZip(arg1)", "arrow.core.padZip"))
fun <A, B> List<A>.padZip(arg1: List<B>): List<Tuple2<Option<A>, Option<B>>> =
    arrow.core.extensions.list.semialign.List.semialign().run {
  arrow.core.ListK(this@padZip).padZip<A, B>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple2<arrow.core.Option<A>, arrow.core.Option<B>>>
}

@JvmName("padZipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.padZip(arg1, arg2)", "arrow.core.padZip"))
fun <A, B, C> List<A>.padZipWith(arg1: List<B>, arg2: Function2<Option<A>, Option<B>, C>): List<C> =
    arrow.core.extensions.list.semialign.List.semialign().run {
  arrow.core.ListK(this@padZipWith).padZipWith<A, B, C>(arrow.core.ListK(arg1), arg2) as
    kotlin.collections.List<C>
}

/**
 * cached extension
 */
@PublishedApi()
internal val semialign_singleton: ListKSemialign = object : arrow.core.extensions.ListKSemialign {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Semialign typeclasses is deprecated. Use concrete methods on List")
  inline fun semialign(): ListKSemialign = semialign_singleton}
