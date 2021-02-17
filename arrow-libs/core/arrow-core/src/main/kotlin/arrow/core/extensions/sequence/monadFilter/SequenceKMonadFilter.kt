package arrow.core.extensions.sequence.monadFilter

import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.extensions.SequenceKMonadFilter
import arrow.typeclasses.MonadFilterSyntax
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
    "this.mapNotNull { arg1(it).orNull() }"
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
  "Monad bindings are deprecated",
  level = DeprecationLevel.WARNING
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

@Deprecated(
  "Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions",
  level = DeprecationLevel.WARNING
)
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "MonadFilter typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun monadFilter(): SequenceKMonadFilter = monadFilter_singleton
}
