package arrow.core.extensions.listk.unalign

import arrow.Kind
import arrow.core.ForListK
import arrow.core.Ior
import arrow.core.ListK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.ListKUnalign
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val unalign_singleton: ListKUnalign = object : arrow.core.extensions.ListKUnalign {}

@JvmName("unalign")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.unalign()", "arrow.core.unalign"))
fun <A, B> unalign(arg0: Kind<ForListK, Ior<A, B>>): Tuple2<Kind<ForListK, A>, Kind<ForListK, B>> =
    arrow.core.ListK
   .unalign()
   .unalign<A, B>(arg0) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>,
    arrow.Kind<arrow.core.ForListK, B>>

@JvmName("unalignWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.unalignWith(arg1)", "arrow.core.unalignWith"))
fun <A, B, C> unalignWith(arg0: Kind<ForListK, C>, arg1: Function1<C, Ior<A, B>>):
    Tuple2<Kind<ForListK, A>, Kind<ForListK, B>> = arrow.core.ListK
   .unalign()
   .unalignWith<A, B, C>(arg0, arg1) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>,
    arrow.Kind<arrow.core.ForListK, B>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Unalign typeclasses is deprecated. Use concrete methods on Iterable")
inline fun Companion.unalign(): ListKUnalign = unalign_singleton
