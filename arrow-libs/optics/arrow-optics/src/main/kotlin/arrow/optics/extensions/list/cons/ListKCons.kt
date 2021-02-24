package arrow.optics.extensions.list.cons

import arrow.core.ListK
import arrow.core.Option
import arrow.core.Tuple2
import arrow.optics.POptional
import arrow.optics.PPrism
import arrow.optics.extensions.ListKCons
import kotlin.collections.List

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the exposed function in the instance for List from the companion object of the typeclass instead.",
  ReplaceWith(
    "Cons.list<A>().firstOption()",
    "arrow.optics.list", "arrow.optics.typeclasses.Cons"
  ),
  DeprecationLevel.WARNING
)
fun <A> firstOption(): POptional<ListK<A>, ListK<A>, A, A> = arrow.optics.extensions.list.cons.List
  .cons<A>()
  .firstOption() as arrow.optics.POptional<arrow.core.ListK<A>, arrow.core.ListK<A>, A, A>

@JvmName("tailOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the exposed function in the instance for List from the companion object of the typeclass instead.",
  ReplaceWith(
    "Cons.list<A>().tailOption()",
    "arrow.optics.list", "arrow.optics.typeclasses.Cons"
  ),
  DeprecationLevel.WARNING
)
fun <A> tailOption(): POptional<ListK<A>, ListK<A>, ListK<A>, ListK<A>> =
  arrow.optics.extensions.list.cons.List
    .cons<A>()
    .tailOption() as arrow.optics.POptional<arrow.core.ListK<A>, arrow.core.ListK<A>,
    arrow.core.ListK<A>, arrow.core.ListK<A>>

@JvmName("cons")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.cons",
  ReplaceWith(
    "cons(tail)",
    "arrow.optics.cons"
  ),
  DeprecationLevel.WARNING
)
infix fun <A> A.cons(tail: List<A>): List<A> =
  arrow.optics.extensions.list.cons.List.cons<A>().run {
    this@cons.cons(arrow.core.ListK(tail)) as kotlin.collections.List<A>
  }

@JvmName("uncons")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated, function is being moved to arrow.optics.uncons",
  ReplaceWith(
    "uncons()",
    "arrow.optics.uncons"
  ),
  DeprecationLevel.WARNING
)
fun <A> List<A>.uncons(): Option<Tuple2<A, ListK<A>>> =
  arrow.optics.extensions.list.cons.List.cons<A>().run {
    arrow.core.ListK(this@uncons).uncons() as arrow.core.Option<arrow.core.Tuple2<A,
        arrow.core.ListK<A>>>
  }

@JvmName("cons")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "arrow.optics.extensions package is being deprecated. Use the exposed function in the instance for List from the companion object of the typeclass instead.",
  ReplaceWith(
    "Cons.list<A>().cons()",
    "arrow.optics.list", "arrow.optics.typeclasses.Cons"
  ),
  DeprecationLevel.WARNING
)
fun <A> cons(): PPrism<ListK<A>, ListK<A>, Tuple2<A, ListK<A>>, Tuple2<A, ListK<A>>> =
  arrow.optics.extensions.list.cons.List
    .cons<A>()
    .cons() as arrow.optics.PPrism<arrow.core.ListK<A>, arrow.core.ListK<A>, arrow.core.Tuple2<A,
      arrow.core.ListK<A>>, arrow.core.Tuple2<A, arrow.core.ListK<A>>>

/**
 * cached extension
 */
@PublishedApi()
internal val cons_singleton: ListKCons<Any?> = object : ListKCons<Any?> {}

@Deprecated("Receiver List object is deprecated, and it will be remove in 0.13.")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Typeclass instance have been moved to the companion object of the typeclass.",
    ReplaceWith(
      "Cons.list<A>()",
      "arrow.optics.list", "arrow.optics.typeclasses.Cons"
    ),
    DeprecationLevel.WARNING
  )
  inline fun <A> cons(): ListKCons<A> = cons_singleton as arrow.optics.extensions.ListKCons<A>
}
