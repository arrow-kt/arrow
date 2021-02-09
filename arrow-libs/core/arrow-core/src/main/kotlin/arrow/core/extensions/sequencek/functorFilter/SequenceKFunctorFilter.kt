package arrow.core.extensions.sequencek.functorFilter

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKFunctorFilter

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
    "this.mapNotNull { arg1(it).orNull() }"
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
    "this.mapNotNull { it.orNull() }"
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
    "this.filter(arg1)"
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
    "this.filterIsInstance<B>()"
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
@Deprecated(
  "FunctorFilter typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
inline fun Companion.functorFilter(): SequenceKFunctorFilter = functorFilter_singleton
