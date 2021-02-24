package arrow.optics.extensions.list.snoc

import arrow.core.ListK
import arrow.core.Option
import arrow.core.Tuple2
import arrow.optics.POptional
import arrow.optics.extensions.ListKSnoc
import kotlin.collections.List

@JvmName("initOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the exposed function in the instance for List from the companion object of the typeclass instead.",
  ReplaceWith(
    "Snoc.list<A>().initOption()",
    "arrow.optics.list", "arrow.optics.typeclasses.Snoc"
  ),
  DeprecationLevel.WARNING
)
fun <A> initOption(): POptional<ListK<A>, ListK<A>, ListK<A>, ListK<A>> =
  arrow.optics.extensions.list.snoc.List
    .snoc<A>()
    .initOption() as arrow.optics.POptional<arrow.core.ListK<A>, arrow.core.ListK<A>,
    arrow.core.ListK<A>, arrow.core.ListK<A>>

@JvmName("lastOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the exposed function in the instance for List from the companion object of the typeclass instead.",
  ReplaceWith(
    "Snoc.list<A>().lastOption()",
    "arrow.optics.list", "arrow.optics.typeclasses.Snoc"
  ),
  DeprecationLevel.WARNING
)
fun <A> lastOption(): POptional<ListK<A>, ListK<A>, A, A> = arrow.optics.extensions.list.snoc.List
  .snoc<A>()
  .lastOption() as arrow.optics.POptional<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

@JvmName("snoc")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.snoc",
  ReplaceWith(
    "snoc(last)",
    "arrow.optics.snoc"
  ),
  DeprecationLevel.WARNING
)
infix fun <A> List<A>.snoc(last: A): List<A> =
  arrow.optics.extensions.list.snoc.List.snoc<A>().run {
    arrow.core.ListK(this@snoc).snoc(last) as kotlin.collections.List<A>
  }

@JvmName("unsnoc")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.unsnoc",
  ReplaceWith(
    "unsnoc()",
    "arrow.optics.unsnoc"
  ),
  DeprecationLevel.WARNING
)
fun <A> List<A>.unsnoc(): Option<Tuple2<ListK<A>, A>> =
  arrow.optics.extensions.list.snoc.List.snoc<A>().run {
    arrow.core.ListK(this@unsnoc).unsnoc() as arrow.core.Option<arrow.core.Tuple2<arrow.core.ListK<A>,
        A>>
  }

/**
 * cached extension
 */
@PublishedApi()
internal val snoc_singleton: ListKSnoc<Any?> = object : ListKSnoc<Any?> {}

@Deprecated("Receiver List object is deprecated, and it will be removed in 0.13.")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Typeclass instance have been moved to the companion object of the typeclass.",
    ReplaceWith(
      "Snoc.list<A>()",
      "arrow.optics.list", "arrow.optics.typeclasses.Snoc"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> snoc(): ListKSnoc<A> = snoc_singleton as arrow.optics.extensions.ListKSnoc<A>
}
