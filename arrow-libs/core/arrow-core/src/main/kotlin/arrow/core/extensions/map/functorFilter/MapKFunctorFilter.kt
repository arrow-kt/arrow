package arrow.core.extensions.map.functorFilter

import arrow.core.Option
import arrow.core.extensions.MapKFunctorFilter
import arrow.core.filterMap
import java.lang.Class
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.collections.filter as _filter
import kotlin.jvm.JvmName

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
  "filterMap { a -> arg1(a).orNull() }",
  "arrow.core.filterMap"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.filterMap(arg1: Function1<A, Option<B>>): Map<K, B> =
    filterMap { a -> arg1(a).orNull() }

@JvmName("flattenOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("filterMap { it.orNull() }", "arrow.core.filterMap"),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, Option<A>>.flattenOption(): Map<K, A> =
  filterMap { it.orNull() }

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
  "filter { (_, a) -> arg1(a) }",
  "kotlin.collections.filter"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.filter(arg1: Function1<A, Boolean>): Map<K, A> =
    _filter { (_, a) -> arg1(a) }

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
  "filterMap { if (arg1.isInstance(it)) arg1.cast(it) else null }",
  "arrow.core.filterMap"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.filterIsInstance(arg1: Class<B>): Map<K, B> =
  filterMap { if (arg1.isInstance(it)) arg1.cast(it) else null }

/**
 * cached extension
 */
@PublishedApi()
internal val functorFilter_singleton: MapKFunctorFilter<Any?> = object : MapKFunctorFilter<Any?> {}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("FunctorFilter typeclasses is deprecated. Use concrete methods on Map")
  inline fun <K> functorFilter(): MapKFunctorFilter<K> = functorFilter_singleton as
      arrow.core.extensions.MapKFunctorFilter<K>}
