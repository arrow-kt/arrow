package arrow.optics.extensions.nonemptylist.each

import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.optics.PTraversal
import arrow.optics.extensions.NonEmptyListEach
import arrow.optics.typeclasses.Each
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val each_singleton: Each<NonEmptyList<Any?>, Any?> = object : NonEmptyListEach<Any?> {}

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
    "Traversal.nonEmptyList<A>()",
    "arrow.optics.Traversal", "arrow.optics.nonEmptyList"
  ),
  DeprecationLevel.WARNING
)
fun <A> each(): PTraversal<NonEmptyList<A>, NonEmptyList<A>, A, A> = arrow.core.NonEmptyList
  .each<A>()
  .each() as arrow.optics.PTraversal<arrow.core.NonEmptyList<A>, arrow.core.NonEmptyList<A>, A, A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Each is being deprecated. Use Traversal directly instead.",
  ReplaceWith(
    "Traversal.nonEmptyList<A>()",
    "arrow.optics.Traversal", "arrow.optics.nonEmptyList"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.each(): Each<NonEmptyList<A>, A> = each_singleton as
  arrow.optics.typeclasses.Each<NonEmptyList<A>, A>
