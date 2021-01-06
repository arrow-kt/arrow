package arrow.core.extensions.listk.semigroup

import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKSemigroup
import kotlin.Any
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semigroup_singleton: ListKSemigroup<Any?> = object : ListKSemigroup<Any?> {}

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("this.plus(arg1)"))
operator fun <A> ListK<A>.plus(arg1: ListK<A>): ListK<A> = arrow.core.ListK.semigroup<A>().run {
  this@plus.plus(arg1) as arrow.core.ListK<A>
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("(arg1?.plus(this) ?: emptyList<A>())"))
fun <A> ListK<A>.maybeCombine(arg1: ListK<A>): ListK<A> = arrow.core.ListK.semigroup<A>().run {
  this@maybeCombine.maybeCombine(arg1) as arrow.core.ListK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("listMonoid<A>()", "arrow.core.listMonoid"))
inline fun <A> Companion.semigroup(): ListKSemigroup<A> = semigroup_singleton as
    arrow.core.extensions.ListKSemigroup<A>
