package arrow.core.extensions.option.monadFilter

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionMonadFilter
import arrow.typeclasses.MonadFilterSyntax
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadFilter_singleton: OptionMonadFilter = object :
    arrow.core.extensions.OptionMonadFilter {}

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
    arrow.core.Option.monadFilter().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.core.Option<B>
}

@JvmName("bindingFilter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "bindingFilter(arg0)",
  "arrow.core.Option.bindingFilter"
  ),
  DeprecationLevel.WARNING
)
fun <B> bindingFilter(arg0: suspend MonadFilterSyntax<ForOption>.() -> B): Option<B> =
    arrow.core.Option
   .monadFilter()
   .bindingFilter<B>(arg0) as arrow.core.Option<B>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.monadFilter(): OptionMonadFilter = monadFilter_singleton
