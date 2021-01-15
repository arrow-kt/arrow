package arrow.core.extensions.id.zip

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.Id.Companion
import arrow.core.Tuple2
import arrow.core.extensions.IdZip
import kotlin.Deprecated
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val zip_singleton: IdZip = object : arrow.core.extensions.IdZip {}

@JvmName("zip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "zip(arg1)",
  "arrow.core.zip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForId, A>.zip(arg1: Kind<ForId, B>): Id<Tuple2<A, B>> = arrow.core.Id.zip().run {
  this@zip.zip<A, B>(arg1) as arrow.core.Id<arrow.core.Tuple2<A, B>>
}

@JvmName("zipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "zipWith(arg1, arg2)",
  "arrow.core.zipWith"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForId, A>.zipWith(arg1: Kind<ForId, B>, arg2: Function2<A, B, C>): Id<C> =
    arrow.core.Id.zip().run {
  this@zipWith.zipWith<A, B, C>(arg1, arg2) as arrow.core.Id<C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.zip(): IdZip = zip_singleton
