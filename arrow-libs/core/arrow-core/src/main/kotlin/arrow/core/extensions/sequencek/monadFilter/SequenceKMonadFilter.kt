package arrow.core.extensions.sequencek.monadFilter

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKMonadFilter
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
internal val monadFilter_singleton: SequenceKMonadFilter = object :
    arrow.core.extensions.SequenceKMonadFilter {}

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
fun <A, B> Kind<ForSequenceK, A>.filterMap(arg1: Function1<A, Option<B>>): SequenceK<B> =
    arrow.core.SequenceK.monadFilter().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.core.SequenceK<B>
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
  "arrow.core.SequenceK.bindingFilter"
  ),
  DeprecationLevel.WARNING
)
fun <B> bindingFilter(arg0: suspend MonadFilterSyntax<ForSequenceK>.() -> B): SequenceK<B> =
    arrow.core.SequenceK
   .monadFilter()
   .bindingFilter<B>(arg0) as arrow.core.SequenceK<B>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.monadFilter(): SequenceKMonadFilter = monadFilter_singleton
