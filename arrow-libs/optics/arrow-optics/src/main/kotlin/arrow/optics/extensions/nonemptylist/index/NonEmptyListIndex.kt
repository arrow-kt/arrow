package arrow.optics.extensions.nonemptylist.index

import arrow.core.NonEmptyList
import arrow.core.NonEmptyList.Companion
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.extensions.NonEmptyListIndex
import arrow.optics.typeclasses.Index
import kotlin.Any
import kotlin.Deprecated
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val index_singleton: Index<NonEmptyList<Any?>, Int, Any?> = object : NonEmptyListIndex<Any?> {}

@JvmName("index")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the function from the instance exposed in the typeclass' companion object instead.",
  ReplaceWith(
    "Index.nonEmptyList<A>().index(i)",
    "arrow.optics.nonEmptyList", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
fun <A> index(i: Int): POptional<NonEmptyList<A>, NonEmptyList<A>, A, A> = arrow.core.NonEmptyList
  .index<A>()
  .index(i) as arrow.optics.POptional<arrow.core.NonEmptyList<A>, arrow.core.NonEmptyList<A>, A, A>

@JvmName("get")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "get(i)",
    "arrow.core.get"
  ),
  DeprecationLevel.WARNING
)
operator fun <A, T> PLens<T, T, NonEmptyList<A>, NonEmptyList<A>>.get(i: Int): POptional<T, T, A, A> =
  arrow.core.NonEmptyList.index<A>().run {
    this@get.get<T>(i) as arrow.optics.POptional<T, T, A, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass",
  ReplaceWith(
    "Index.nonEmptyList<A>()",
    "arrow.optics.nonEmptyList", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.index(): Index<NonEmptyList<A>, Int, A> =
  index_singleton as arrow.optics.typeclasses.Index<NonEmptyList<A>, Int, A>
