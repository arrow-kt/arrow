package arrow.core.extensions.sequence.functorFilter

import arrow.core.Option
import arrow.core.extensions.SequenceKFunctorFilter
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
    "this.mapNotNull { it.orNull() }"
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
    "this.filter(arg1)"
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
    "this.filterIsInstance<B>()"
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
    "FunctorFilter typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun functorFilter(): SequenceKFunctorFilter = functorFilter_singleton
}
