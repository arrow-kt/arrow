package arrow.core.extensions.list.zip

import arrow.core.Tuple2
import arrow.core.extensions.ListKZip
import arrow.core.toTuple2
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.zip as _zip
import kotlin.jvm.JvmName

@JvmName("zip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("zip(arg1).map { it.toTuple2() }", "arrow.core.toTuple2"))
fun <A, B> List<A>.zip(arg1: List<B>): List<Tuple2<A, B>> =
  _zip(arg1).map { it.toTuple2() }

@JvmName("zipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("zip(arg1, arg2)"))
fun <A, B, C> List<A>.zipWith(arg1: List<B>, arg2: Function2<A, B, C>): List<C> =
  _zip(arg1, arg2)

/**
 * cached extension
 */
@PublishedApi()
internal val zip_singleton: ListKZip = object : arrow.core.extensions.ListKZip {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Zip typeclasses is deprecated. Use concrete methods on Iterable")
  inline fun zip(): ListKZip = zip_singleton}
