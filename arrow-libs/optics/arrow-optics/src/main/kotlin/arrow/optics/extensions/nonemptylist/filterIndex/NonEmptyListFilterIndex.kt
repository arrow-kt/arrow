package arrow.optics.extensions.nonemptylist.filterIndex

import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.NonEmptyListFilterIndex
import arrow.optics.typeclasses.FilterIndex
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val filterIndex_singleton: FilterIndex<NonEmptyList<Any?>, Int, Any?> =
  object : NonEmptyListFilterIndex<Any?> {}

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the function from the instance exposed in the typeclass' companion object instead.",
  ReplaceWith(
    "FilterIndex.nonEmptyList<A>().filter(p)",
    "arrow.optics.nonEmptyList", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
fun <A> filter(p: Function1<Int, Boolean>): PTraversal<NonEmptyList<A>, NonEmptyList<A>, A, A> =
    arrow.core.NonEmptyList
   .filterIndex<A>()
   .filter(p) as arrow.optics.PTraversal<arrow.core.NonEmptyList<A>, arrow.core.NonEmptyList<A>, A,
    A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass",
  ReplaceWith(
    "FilterIndex.nonEmptyList<A>()",
    "arrow.optics.nonEmptyList", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.filterIndex(): FilterIndex<NonEmptyList<A>, Int, A> =
  filterIndex_singleton as arrow.optics.typeclasses.FilterIndex<NonEmptyList<A>, Int, A>
