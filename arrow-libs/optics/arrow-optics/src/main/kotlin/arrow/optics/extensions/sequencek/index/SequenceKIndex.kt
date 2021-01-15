package arrow.optics.extensions.sequencek.index

import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.extensions.SequenceKIndex

/**
 * cached extension
 */
@PublishedApi()
internal val index_singleton: SequenceKIndex<Any?> = object : SequenceKIndex<Any?> {}

@JvmName("index")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the exposed function in the instance for Sequence from the companion object of the typeclass instead.",
  ReplaceWith(
    "Index.sequence<A>().index(i)",
    "arrow.optics.sequence", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
fun <A> index(i: Int): POptional<SequenceK<A>, SequenceK<A>, A, A> = arrow.core.SequenceK
   .index<A>()
   .index(i) as arrow.optics.POptional<arrow.core.SequenceK<A>, arrow.core.SequenceK<A>, A, A>

@JvmName("get")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated, and it will be removed in 0.13.",
  ReplaceWith(
    "this compose Index.sequence<A>().index(i)",
    "arrow.optics.compose", "arrow.optics.sequence", "arrow.optics.typeclasses.Index"
  ),
  level = DeprecationLevel.WARNING
)
operator fun <A, T> PLens<T, T, SequenceK<A>, SequenceK<A>>.get(i: Int): POptional<T, T, A, A> =
    arrow.core.SequenceK.index<A>().run {
  this@get.get<T>(i) as arrow.optics.POptional<T, T, A, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "SequenceK is being deprecated. Use the instance for Sequence from the companion object of the typeclass.",
  ReplaceWith(
    "Index.sequence<A>()",
    "arrow.optics.sequence", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.index(): SequenceKIndex<A> = index_singleton as
    arrow.optics.extensions.SequenceKIndex<A>
