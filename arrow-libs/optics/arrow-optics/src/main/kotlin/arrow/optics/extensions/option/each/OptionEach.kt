package arrow.optics.extensions.option.each

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.OptionEach

/**
 * cached extension
 */
@PublishedApi()
internal val each_singleton: OptionEach<Any?> = object : OptionEach<Any?> {}

@JvmName("each")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "Each is being deprecated. Use Traversal directly instead.",
  ReplaceWith(
  "Traversal.option<A>()",
  "arrow.optics.traversal", "arrow.optics.option"
  ),
  DeprecationLevel.WARNING
)
fun <A> each(): PTraversal<Option<A>, Option<A>, A, A> = arrow.core.Option
   .each<A>()
   .each() as arrow.optics.PTraversal<arrow.core.Option<A>, arrow.core.Option<A>, A, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Each is being deprecated. Use Traversal directly instead.",
  ReplaceWith(
    "Traversal.option<A>()",
    "arrow.optics.traversal", "arrow.optics.option"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.each(): OptionEach<A> = each_singleton as
    arrow.optics.extensions.OptionEach<A>
