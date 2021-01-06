package arrow.core.extensions.listk.functorFilter

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Option
import arrow.core.extensions.ListKFunctorFilter
import java.lang.Class
import kotlin.Boolean
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val functorFilter_singleton: ListKFunctorFilter = object :
    arrow.core.extensions.ListKFunctorFilter {}

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("mapNotNull { arg1(it).orNull() }"))
fun <A, B> Kind<ForListK, A>.filterMap(arg1: Function1<A, Option<B>>): ListK<B> =
    arrow.core.ListK.functorFilter().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.core.ListK<B>
}

@JvmName("flattenOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> Kind<ForListK, Option<A>>.flattenOption(): ListK<A> = arrow.core.ListK.functorFilter().run {
  this@flattenOption.flattenOption<A>() as arrow.core.ListK<A>
}

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("mapNotNull { it.orNull() }"))
fun <A> Kind<ForListK, A>.filter(arg1: Function1<A, Boolean>): ListK<A> =
    arrow.core.ListK.functorFilter().run {
  this@filter.filter<A>(arg1) as arrow.core.ListK<A>
}

@JvmName("filterIsInstance")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("filter(arg1::isInstance).map { arg1.cast(it) }"))
fun <A, B> Kind<ForListK, A>.filterIsInstance(arg1: Class<B>): ListK<B> =
    arrow.core.ListK.functorFilter().run {
  this@filterIsInstance.filterIsInstance<A, B>(arg1) as arrow.core.ListK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Functor typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.functorFilter(): ListKFunctorFilter = functorFilter_singleton
