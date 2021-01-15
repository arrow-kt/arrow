package arrow.core.extensions.sequencek.functorFilter

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKFunctorFilter
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
internal val functorFilter_singleton: SequenceKFunctorFilter = object :
    arrow.core.extensions.SequenceKFunctorFilter {}

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
    arrow.core.SequenceK.functorFilter().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.core.SequenceK<B>
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
fun <A> Kind<ForSequenceK, Option<A>>.flattenOption(): SequenceK<A> =
    arrow.core.SequenceK.functorFilter().run {
  this@flattenOption.flattenOption<A>() as arrow.core.SequenceK<A>
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
fun <A> Kind<ForSequenceK, A>.filter(arg1: Function1<A, Boolean>): SequenceK<A> =
    arrow.core.SequenceK.functorFilter().run {
  this@filter.filter<A>(arg1) as arrow.core.SequenceK<A>
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
fun <A, B> Kind<ForSequenceK, A>.filterIsInstance(arg1: Class<B>): SequenceK<B> =
    arrow.core.SequenceK.functorFilter().run {
  this@filterIsInstance.filterIsInstance<A, B>(arg1) as arrow.core.SequenceK<B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.functorFilter(): SequenceKFunctorFilter = functorFilter_singleton
