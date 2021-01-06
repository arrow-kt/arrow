package arrow.core.extensions.list.applicative

import arrow.core.extensions.ListKApplicative
import arrow.typeclasses.Monoid
import arrow.core.replicate as _replicate
import kotlin.Function1
import kotlin.collections.map as _map
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("listOf(this)"))
fun <A> A.just(): List<A> =
  listOf(this)

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("listOf(Unit)"))
fun unit(): List<Unit> =
  listOf(Unit)

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("map(arg1)"))
fun <A, B> List<A>.map(arg1: Function1<A, B>): List<B> =
  _map(arg1)

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("replicate(arg1)", "arrow.core.replicate"))
fun <A> List<A>.replicate(arg1: Int): List<List<A>> =
  _replicate(arg1)

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated", ReplaceWith("replicate(arg1, arg2)", "arrow.core.replicate"))
fun <A> List<A>.replicate(arg1: Int, arg2: Monoid<A>): List<A> =
  _replicate(arg1, arg2)

/**
 * cached extension
 */
@PublishedApi()
internal val applicative_singleton: ListKApplicative = object :
  arrow.core.extensions.ListKApplicative {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Applicative typeclasses is deprecated. Use concrete methods on List")
  inline fun applicative(): ListKApplicative = applicative_singleton
}
