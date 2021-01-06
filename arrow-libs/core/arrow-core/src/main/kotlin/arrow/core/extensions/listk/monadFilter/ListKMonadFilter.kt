package arrow.core.extensions.listk.monadFilter

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Option
import arrow.core.extensions.ListKMonadFilter
import arrow.typeclasses.MonadFilterSyntax
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadFilter_singleton: ListKMonadFilter = object :
    arrow.core.extensions.ListKMonadFilter {}

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("mapNotNull { arg1(it).orNull() }"))
fun <A, B> Kind<ForListK, A>.filterMap(arg1: Function1<A, Option<B>>): ListK<B> =
    arrow.core.ListK.monadFilter().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.core.ListK<B>
}

@JvmName("bindingFilter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Monad bindings are deprecated")
fun <B> bindingFilter(arg0: suspend MonadFilterSyntax<ForListK>.() -> B): ListK<B> =
    arrow.core.ListK
   .monadFilter()
   .bindingFilter<B>(arg0) as arrow.core.ListK<B>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("MonadFilter typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.monadFilter(): ListKMonadFilter = monadFilter_singleton
