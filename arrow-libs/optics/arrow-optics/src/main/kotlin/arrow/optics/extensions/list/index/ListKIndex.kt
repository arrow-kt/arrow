package arrow.optics.extensions.list.index

import arrow.core.ListK
import arrow.optics.POptional
import arrow.optics.extensions.ListKIndex

@JvmName("index")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the exposed function in the instance for List from the companion object of the typeclass instead.",
  ReplaceWith(
    "Index.list<A>().index(i)",
    "arrow.optics.list", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
fun <A> index(i: Int): POptional<ListK<A>, ListK<A>, A, A> = arrow.optics.extensions.list.index.List
  .index<A>()
  .index(i) as arrow.optics.POptional<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

/**
 * cached extension
 */
@PublishedApi()
internal val index_singleton: ListKIndex<Any?> = object : ListKIndex<Any?> {}

@Deprecated("Receiver List object is deprecated, and it will be removed in 0.13.")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Typeclass instance have been moved to the companion object of the typeclass.",
    ReplaceWith(
      "Index.list<A>()",
      "arrow.optics.list", "arrow.optics.typeclasses.Index"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> index(): ListKIndex<A> = index_singleton as arrow.optics.extensions.ListKIndex<A>
}
