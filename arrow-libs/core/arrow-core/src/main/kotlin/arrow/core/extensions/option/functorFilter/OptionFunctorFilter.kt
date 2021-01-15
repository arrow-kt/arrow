package arrow.core.extensions.option.functorFilter

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionFunctorFilter
import java.lang.Class
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val functorFilter_singleton: OptionFunctorFilter = object :
    arrow.core.extensions.OptionFunctorFilter {}

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "filterMap(arg1)",
  "arrow.core.filterMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.filterMap(arg1: Function1<A, Option<B>>): Option<B> =
    arrow.core.Option.functorFilter().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.core.Option<B>
}

@JvmName("flattenOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "flattenOption()",
  "arrow.core.flattenOption"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, Option<A>>.flattenOption(): Option<A> =
    arrow.core.Option.functorFilter().run {
  this@flattenOption.flattenOption<A>() as arrow.core.Option<A>
}

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "filter(arg1)",
  "arrow.core.filter"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.filter(arg1: Function1<A, Boolean>): Option<A> =
    arrow.core.Option.functorFilter().run {
  this@filter.filter<A>(arg1) as arrow.core.Option<A>
}

@JvmName("filterIsInstance")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "filterIsInstance(arg1)",
  "arrow.core.filterIsInstance"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.filterIsInstance(arg1: Class<B>): Option<B> =
    arrow.core.Option.functorFilter().run {
  this@filterIsInstance.filterIsInstance<A, B>(arg1) as arrow.core.Option<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.functorFilter(): OptionFunctorFilter = functorFilter_singleton
