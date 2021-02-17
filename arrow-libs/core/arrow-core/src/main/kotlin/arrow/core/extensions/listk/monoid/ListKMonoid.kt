package arrow.core.extensions.listk.monoid

import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKMonoid
import kotlin.Any
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monoid_singleton: ListKMonoid<Any?> = object : ListKMonoid<Any?> {}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.fold(emptyList()) { acc, l -> acc + l }"))
fun <A> Collection<ListK<A>>.combineAll(): ListK<A> = arrow.core.ListK.monoid<A>().run {
  this@combineAll.combineAll() as arrow.core.ListK<A>
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("arg0.fold(emptyList()) { acc, l -> acc + l }"))
fun <A> combineAll(arg0: List<ListK<A>>): ListK<A> = arrow.core.ListK
  .monoid<A>()
  .combineAll(arg0) as arrow.core.ListK<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("Monoid.list<A>()", "arrow.core.list", "arrow.core.Monoid"))
inline fun <A> Companion.monoid(): ListKMonoid<A> = monoid_singleton as
  arrow.core.extensions.ListKMonoid<A>
