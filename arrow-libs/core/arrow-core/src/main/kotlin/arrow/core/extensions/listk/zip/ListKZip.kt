package arrow.core.extensions.listk.zip

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.ListK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.ListKZip
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val zip_singleton: ListKZip = object : arrow.core.extensions.ListKZip {}

@JvmName("zip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("zip(arg1).map { it.toTuple2() }", "arrow.core.toTuple2"))
fun <A, B> Kind<ForListK, A>.zip(arg1: Kind<ForListK, B>): ListK<Tuple2<A, B>> =
  arrow.core.ListK.zip().run {
    this@zip.zip<A, B>(arg1) as arrow.core.ListK<arrow.core.Tuple2<A, B>>
  }

@JvmName("zipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("zip(arg1, arg2)"))
fun <A, B, C> Kind<ForListK, A>.zipWith(arg1: Kind<ForListK, B>, arg2: Function2<A, B, C>): ListK<C> =
  arrow.core.ListK.zip().run {
    this@zipWith.zipWith<A, B, C>(arg1, arg2) as arrow.core.ListK<C>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Zip typeclasses is deprecated. Use concrete methods on Iterable")
inline fun Companion.zip(): ListKZip = zip_singleton
