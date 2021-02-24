package arrow.core.extensions.list.unalign

import arrow.Kind
import arrow.core.ForListK
import arrow.core.Ior
import arrow.core.Tuple2
import arrow.core.extensions.ListKUnalign
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("unalign")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.unalign()", "arrow.core.unalign"))
fun <A, B> unalign(arg0: List<Ior<A, B>>): Tuple2<Kind<ForListK, A>, Kind<ForListK, B>> =
  arrow.core.extensions.list.unalign.List
    .unalign()
    .unalign<A, B>(arrow.core.ListK(arg0)) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>,
    arrow.Kind<arrow.core.ForListK, B>>

@JvmName("unalignWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.unalign(arg1)", "arrow.core.unalign"))
fun <A, B, C> unalignWith(arg0: List<C>, arg1: Function1<C, Ior<A, B>>): Tuple2<Kind<ForListK, A>,
  Kind<ForListK, B>> = arrow.core.extensions.list.unalign.List
  .unalign()
  .unalignWith<A, B, C>(arrow.core.ListK(arg0), arg1) as
  arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>, arrow.Kind<arrow.core.ForListK, B>>

/**
 * cached extension
 */
@PublishedApi()
internal val unalign_singleton: ListKUnalign = object : arrow.core.extensions.ListKUnalign {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Unalign typeclasses is deprecated. Use concrete methods on Iterable")
  inline fun unalign(): ListKUnalign = unalign_singleton
}
