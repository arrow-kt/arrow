package arrow.optics.extensions.listk.snoc

import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Option
import arrow.core.Tuple2
import arrow.optics.POptional
import arrow.optics.extensions.ListKSnoc
import kotlin.Any
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val snoc_singleton: ListKSnoc<Any?> = object : ListKSnoc<Any?> {}

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
fun <A> initOption(): POptional<ListK<A>, ListK<A>, ListK<A>, ListK<A>> = arrow.core.ListK
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
fun <A> lastOption(): POptional<ListK<A>, ListK<A>, A, A> = arrow.core.ListK
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
  "ListK is being deprecated, use the snoc function defined for List instead.",
  ReplaceWith(
    "snoc(last)",
    "arrow.optics.snoc"
  ),
  DeprecationLevel.WARNING
)
infix fun <A> ListK<A>.snoc(last: A): ListK<A> = arrow.core.ListK.snoc<A>().run {
  this@snoc.snoc(last) as arrow.core.ListK<A>
}

@JvmName("unsnoc")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "ListK is being deprecated. Use the unsnoc function defined for List instead.",
  ReplaceWith(
    "unsnoc()",
    "arrow.optics.unsnoc"
  ),
  DeprecationLevel.WARNING
)
fun <A> ListK<A>.unsnoc(): Option<Tuple2<ListK<A>, A>> = arrow.core.ListK.snoc<A>().run {
  this@unsnoc.unsnoc() as arrow.core.Option<arrow.core.Tuple2<arrow.core.ListK<A>, A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "ListK is being deprecated. Use the instance for List from the companion object of the typeclass.",
  ReplaceWith(
    "Snoc.list<A>()",
    "arrow.optics.list", "arrow.optics.typeclasses.Snoc"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.snoc(): ListKSnoc<A> = snoc_singleton as
  arrow.optics.extensions.ListKSnoc<A>
