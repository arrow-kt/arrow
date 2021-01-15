package arrow.core.extensions.const.traverseFilter

import arrow.Kind
import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.ForConst
import arrow.core.Option
import arrow.core.extensions.ConstTraverseFilter
import arrow.typeclasses.Applicative
import java.lang.Class
import kotlin.Any
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
internal val traverseFilter_singleton: ConstTraverseFilter<Any?> = object :
    ConstTraverseFilter<Any?> {}

@JvmName("traverseFilter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "traverseFilter(arg1, arg2)",
  "arrow.core.traverseFilter"
  ),
  DeprecationLevel.WARNING
)
fun <X, G, A, B> Kind<Kind<ForConst, X>, A>.traverseFilter(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, Option<B>>>
): Kind<G, Kind<Kind<ForConst, X>, B>> =
    arrow.core.Const.traverseFilter<X>().run {
  this@traverseFilter.traverseFilter<G, A, B>(arg1, arg2) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForConst, X>, B>>
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
  "filterMap(arg1)",
  "arrow.core.filterMap"
  ),
  DeprecationLevel.WARNING
)
fun <X, A, B> Kind<Kind<ForConst, X>, A>.filterMap(arg1: Function1<A, Option<B>>): Const<X, B> =
    arrow.core.Const.traverseFilter<X>().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.core.Const<X, B>
}

@JvmName("filterA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "filterA(arg1, arg2)",
  "arrow.core.filterA"
  ),
  DeprecationLevel.WARNING
)
fun <X, G, A> Kind<Kind<ForConst, X>, A>.filterA(
  arg1: Function1<A, Kind<G, Boolean>>,
  arg2: Applicative<G>
): Kind<G, Kind<Kind<ForConst, X>, A>> =
    arrow.core.Const.traverseFilter<X>().run {
  this@filterA.filterA<G, A>(arg1, arg2) as arrow.Kind<G, arrow.Kind<arrow.Kind<arrow.core.ForConst,
    X>, A>>
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
fun <X, A> Kind<Kind<ForConst, X>, A>.filter(arg1: Function1<A, Boolean>): Const<X, A> =
    arrow.core.Const.traverseFilter<X>().run {
  this@filter.filter<A>(arg1) as arrow.core.Const<X, A>
}

@JvmName("traverseFilterIsInstance")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "traverseFilterIsInstance(arg1, arg2)",
  "arrow.core.traverseFilterIsInstance"
  ),
  DeprecationLevel.WARNING
)
fun <X, G, A, B> Kind<Kind<ForConst, X>, A>.traverseFilterIsInstance(
  arg1: Applicative<G>,
  arg2: Class<B>
): Kind<G, Kind<Kind<ForConst, X>, B>> =
    arrow.core.Const.traverseFilter<X>().run {
  this@traverseFilterIsInstance.traverseFilterIsInstance<G, A, B>(arg1, arg2) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForConst, X>, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <X> Companion.traverseFilter(): ConstTraverseFilter<X> = traverseFilter_singleton as
    arrow.core.extensions.ConstTraverseFilter<X>
