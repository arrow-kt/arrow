package arrow.core.extensions.list.monoid

import arrow.core.ListK
import arrow.core.extensions.ListKMonoid
import kotlin.Any
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.fold(emptyList()) { acc, l -> acc + l }"))
fun <A> Collection<ListK<A>>.combineAll(): List<A> =
  arrow.core.extensions.list.monoid.List.monoid<A>().run {
    this@combineAll.combineAll() as kotlin.collections.List<A>
  }

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.fold(emptyList()) { acc, l -> acc + l }"))
fun <A> combineAll(arg0: List<ListK<A>>): List<A> =
  arg0.fold(emptyList()) { acc, l -> acc + l }

/**
 * cached extension
 */
@PublishedApi()
internal val monoid_singleton: ListKMonoid<Any?> = object : ListKMonoid<Any?> {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("@extension projected functions are deprecated", ReplaceWith("Monoid.list<A>()", "arrow.core.list", "arrow.core.Monoid"))
  inline fun <A> monoid(): ListKMonoid<A> = monoid_singleton as
    arrow.core.extensions.ListKMonoid<A>
}
