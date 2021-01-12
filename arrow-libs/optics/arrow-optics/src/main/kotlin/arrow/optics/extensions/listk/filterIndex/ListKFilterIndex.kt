package arrow.optics.extensions.listk.filterIndex

import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.ListKFilterIndex
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
internal val filterIndex_singleton: ListKFilterIndex<Any?> = object : ListKFilterIndex<Any?> {}

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
fun <A> filter(p: Function1<Int, Boolean>): PTraversal<ListK<A>, ListK<A>, A, A> = arrow.core.ListK
   .filterIndex<A>()
   .filter(p) as arrow.optics.PTraversal<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "ListK is being deprecated. Use the instance for List from the companion object of the typeclass.",
  ReplaceWith(
    "FilterIndex.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.filterIndex(): ListKFilterIndex<A> =
  filterIndex_singleton as arrow.optics.extensions.ListKFilterIndex<A>
