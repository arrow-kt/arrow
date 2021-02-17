package arrow.core.extensions.option.traverseFilter

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionTraverseFilter
import arrow.typeclasses.Applicative

/**
 * cached extension
 */
@PublishedApi()
internal val traverseFilter_singleton: OptionTraverseFilter = object :
  arrow.core.extensions.OptionTraverseFilter {}

@JvmName("traverseFilter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Applicative typeclass is deprecated, Replace with traverseFilter, traverseFilterEither or traverseFilterValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Kind<ForOption, A>.traverseFilter(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, Option<B>>>
): Kind<G, Kind<ForOption, B>> = arrow.core.Option.traverseFilter().run {
  this@traverseFilter.traverseFilter<G, A, B>(arg1, arg2) as arrow.Kind<G,
    arrow.Kind<arrow.core.ForOption, B>>
}

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
    "this.mapNotNull(arg1.andThen { it.orNull() })",
    "arrow.core.andThen"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForOption, A>.filterMap(arg1: Function1<A, Option<B>>): Option<B> =
  arrow.core.Option.traverseFilter().run {
    this@filterMap.filterMap<A, B>(arg1) as arrow.core.Option<B>
  }

@JvmName("filterA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Applicative typeclass is deprecated, Replace with filterEither, filterIterable or filterValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A> Kind<ForOption, A>.filterA(arg1: Function1<A, Kind<G, Boolean>>, arg2: Applicative<G>):
  Kind<G, Kind<ForOption, A>> = arrow.core.Option.traverseFilter().run {
    this@filterA.filterA<G, A>(arg1, arg2) as arrow.Kind<G, arrow.Kind<arrow.core.ForOption, A>>
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
    "filter(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForOption, A>.filter(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Option.traverseFilter().run {
    this@filter.filter<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("traverseFilterIsInstance")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Applicative typeclass is deprecated, Replace with traverseFilterIsInstance, traverseFilterIsInstanceEither or traverseFilterIsInstanceValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <G, A, B> Kind<ForOption, A>.traverseFilterIsInstance(arg1: Applicative<G>, arg2: Class<B>):
  Kind<G, Kind<ForOption, B>> = arrow.core.Option.traverseFilter().run {
    this@traverseFilterIsInstance.traverseFilterIsInstance<G, A, B>(arg1, arg2) as arrow.Kind<G,
      arrow.Kind<arrow.core.ForOption, B>>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "TraverseFilter typeclass is deprecated. Use concrete methods on Option",
  level = DeprecationLevel.WARNING
)
inline fun Companion.traverseFilter(): OptionTraverseFilter = traverseFilter_singleton
