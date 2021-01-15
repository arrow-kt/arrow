package arrow.core.extensions.sequence.monadFilter

import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.extensions.SequenceKMonadFilter
import arrow.typeclasses.MonadFilterSyntax
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
    arrow.core.extensions.sequence.monadFilter.Sequence.monadFilter().run {
  arrow.core.SequenceK(this@filterMap).filterMap<A, B>(arg1) as kotlin.sequences.Sequence<B>
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
  "arrow.core.extensions.sequence.monadFilter.Sequence.bindingFilter"
  ),
  DeprecationLevel.WARNING
)
fun <B> bindingFilter(arg0: suspend MonadFilterSyntax<ForSequenceK>.() -> B): Sequence<B> =
    arrow.core.extensions.sequence.monadFilter.Sequence
   .monadFilter()
   .bindingFilter<B>(arg0) as kotlin.sequences.Sequence<B>

/**
 * cached extension
 */
@PublishedApi()
internal val monadFilter_singleton: SequenceKMonadFilter = object :
    arrow.core.extensions.SequenceKMonadFilter {}

object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monadFilter(): SequenceKMonadFilter = monadFilter_singleton}
