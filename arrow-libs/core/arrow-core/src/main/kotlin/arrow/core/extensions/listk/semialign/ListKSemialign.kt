package arrow.core.extensions.listk.semialign

import arrow.Kind
import arrow.core.ForListK
import arrow.core.Ior
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.ListKSemialign
import arrow.typeclasses.Semigroup
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semialign_singleton: ListKSemialign = object : arrow.core.extensions.ListKSemialign {}

@JvmName("align")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.align(arg1)", "arrow.core.align"))
fun <A, B> align(arg0: Kind<ForListK, A>, arg1: Kind<ForListK, B>): ListK<Ior<A, B>> =
  arrow.core.ListK
    .semialign()
    .align<A, B>(arg0, arg1) as arrow.core.ListK<arrow.core.Ior<A, B>>

@JvmName("alignWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.alignWith(arg1, arg2)", "arrow.core.alignWith"))
fun <A, B, C> alignWith(
  arg0: Kind<ForListK, A>,
  arg1: Kind<ForListK, B>,
  arg2: Function1<Ior<A, B>, C>
): ListK<C> = arrow.core.ListK
  .semialign()
  .alignWith<A, B, C>(arg0, arg1, arg2) as arrow.core.ListK<C>

@JvmName("salign")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.salign(arg1, arg2)", "arrow.core.salign"))
fun <A> Kind<ForListK, A>.salign(arg1: Semigroup<A>, arg2: Kind<ForListK, A>): ListK<A> =
  arrow.core.ListK.semialign().run {
    this@salign.salign<A>(arg1, arg2) as arrow.core.ListK<A>
  }

@JvmName("padZip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.padZip(arg1)", "arrow.core.padZip"))
fun <A, B> Kind<ForListK, A>.padZip(arg1: Kind<ForListK, B>): ListK<Tuple2<Option<A>, Option<B>>> =
  arrow.core.ListK.semialign().run {
    this@padZip.padZip<A, B>(arg1) as arrow.core.ListK<arrow.core.Tuple2<arrow.core.Option<A>,
        arrow.core.Option<B>>>
  }

@JvmName("padZipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.padZipWith(arg1, arg2)", "arrow.core.padZipWith"))
fun <A, B, C> Kind<ForListK, A>.padZipWith(
  arg1: Kind<ForListK, B>,
  arg2: Function2<Option<A>,
    Option<B>, C>
): ListK<C> = arrow.core.ListK.semialign().run {
  this@padZipWith.padZipWith<A, B, C>(arg1, arg2) as arrow.core.ListK<C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Semialign typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.semialign(): ListKSemialign = semialign_singleton
