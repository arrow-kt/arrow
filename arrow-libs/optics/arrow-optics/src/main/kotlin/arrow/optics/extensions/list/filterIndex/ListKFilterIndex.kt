package arrow.optics.extensions.list.filterIndex

import arrow.core.ListK
import arrow.optics.PTraversal
import arrow.optics.extensions.ListKFilterIndex

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the exposed function in the instance for List from the companion object of the typeclass instead.",
  ReplaceWith(
    "FilterIndex.list<A>().filter(p)",
    "arrow.optics.list", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
fun <A> filter(p: Function1<Int, Boolean>): PTraversal<ListK<A>, ListK<A>, A, A> =
  arrow.optics.extensions.list.filterIndex.List
    .filterIndex<A>()
    .filter(p) as arrow.optics.PTraversal<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

/**
 * cached extension
 */
@PublishedApi()
internal val filterIndex_singleton: ListKFilterIndex<Any?> = object : ListKFilterIndex<Any?> {}

@Deprecated("Receiver List object is deprecated, and it will be removed in 0.13.")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Typeclass instance have been moved to the companion object of the typeclass.",
    ReplaceWith(
      "FilterIndex.list<A>()",
      "arrow.optics.list", "arrow.optics.typeclasses.FilterIndex"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> filterIndex(): ListKFilterIndex<A> = filterIndex_singleton as
    arrow.optics.extensions.ListKFilterIndex<A>
}
