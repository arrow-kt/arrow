package arrow.core.extensions.listk.unzip

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.ListKUnzip
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val unzip_singleton: ListKUnzip = object : arrow.core.extensions.ListKUnzip {}

@JvmName("unzip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("unzip()", "arrow.core.unzip"))
fun <A, B> Kind<ForListK, Tuple2<A, B>>.unzip(): Tuple2<Kind<ForListK, A>, Kind<ForListK, B>> =
  arrow.core.ListK.unzip().run {
    this@unzip.unzip<A, B>() as arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>,
      arrow.Kind<arrow.core.ForListK, B>>
  }

@JvmName("unzipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("unzipWith(arg1)", "arrow.core.unzipWith"))
fun <A, B, C> Kind<ForListK, C>.unzipWith(arg1: Function1<C, Tuple2<A, B>>): Tuple2<Kind<ForListK,
    A>, Kind<ForListK, B>> = arrow.core.ListK.unzip().run {
  this@unzipWith.unzipWith<A, B, C>(arg1) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>,
    arrow.Kind<arrow.core.ForListK, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Unzip typeclasses is deprecated. Use concrete methods on Iterable")
inline fun Companion.unzip(): ListKUnzip = unzip_singleton
