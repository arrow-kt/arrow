package arrow.core.extensions.listk.applicative

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKApplicative
import arrow.typeclasses.Monoid
import kotlin.Function1
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val applicative_singleton: ListKApplicative = object :
    arrow.core.extensions.ListKApplicative {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("listOf(this)"))
fun <A> A.just(): ListK<A> = arrow.core.ListK.applicative().run {
  this@just.just<A>() as arrow.core.ListK<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("listOf(Unit)"))
fun unit(): ListK<Unit> = arrow.core.ListK
   .applicative()
   .unit() as arrow.core.ListK<kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <A, B> Kind<ForListK, A>.map(arg1: Function1<A, B>): ListK<B> =
    arrow.core.ListK.applicative().run {
  this@map.map<A, B>(arg1) as arrow.core.ListK<B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("replicate(arg1)", "arrow.core.replicate"))
fun <A> Kind<ForListK, A>.replicate(arg1: Int): ListK<List<A>> =
    arrow.core.ListK.applicative().run {
  this@replicate.replicate<A>(arg1) as arrow.core.ListK<kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("replicate(arg1, arg2)", "arrow.core.replicate"))
fun <A> Kind<ForListK, A>.replicate(arg1: Int, arg2: Monoid<A>): ListK<A> =
    arrow.core.ListK.applicative().run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.core.ListK<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Applicative typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.applicative(): ListKApplicative = applicative_singleton
