package arrow.optics.extensions.listk.each

import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.ListKEach

/**
 * cached extension
 */
@PublishedApi()
internal val each_singleton: ListKEach<Any?> = object : ListKEach<Any?> {}

@JvmName("each")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Each is being deprecated. Use the instance for List from Traversal's companion object instead.",
  ReplaceWith(
    "Traversal.list<A>()",
    "arrow.optics.Traversal", "arrow.optics.list"
  ),
  DeprecationLevel.WARNING
)
fun <A> each(): PTraversal<ListK<A>, ListK<A>, A, A> = arrow.core.ListK
  .each<A>()
  .each() as arrow.optics.PTraversal<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Each is being deprecated. Use the instance for List from Traversal's companion object instead.",
  ReplaceWith(
    "Traversal.list<A>()",
    "arrow.optics.Traversal", "arrow.optics.list"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.each(): ListKEach<A> = each_singleton as
  arrow.optics.extensions.ListKEach<A>
