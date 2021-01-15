package arrow.core.extensions.sequence.functorFilter

import arrow.core.Option
import arrow.core.extensions.SequenceKFunctorFilter
import java.lang.Class
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName
import kotlin.sequences.Sequence

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
fun <A, B> Sequence<A>.filterMap(arg1: Function1<A, Option<B>>): Sequence<B> =
    arrow.core.extensions.sequence.functorFilter.Sequence.functorFilter().run {
  arrow.core.SequenceK(this@filterMap).filterMap<A, B>(arg1) as kotlin.sequences.Sequence<B>
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
fun <A> Sequence<Option<A>>.flattenOption(): Sequence<A> =
    arrow.core.extensions.sequence.functorFilter.Sequence.functorFilter().run {
  arrow.core.SequenceK(this@flattenOption).flattenOption<A>() as kotlin.sequences.Sequence<A>
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
fun <A> Sequence<A>.filter(arg1: Function1<A, Boolean>): Sequence<A> =
    arrow.core.extensions.sequence.functorFilter.Sequence.functorFilter().run {
  arrow.core.SequenceK(this@filter).filter<A>(arg1) as kotlin.sequences.Sequence<A>
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
fun <A, B> Sequence<A>.filterIsInstance(arg1: Class<B>): Sequence<B> =
    arrow.core.extensions.sequence.functorFilter.Sequence.functorFilter().run {
  arrow.core.SequenceK(this@filterIsInstance).filterIsInstance<A, B>(arg1) as
    kotlin.sequences.Sequence<B>
}

/**
 * cached extension
 */
@PublishedApi()
internal val functorFilter_singleton: SequenceKFunctorFilter = object :
    arrow.core.extensions.SequenceKFunctorFilter {}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun functorFilter(): SequenceKFunctorFilter = functorFilter_singleton}
