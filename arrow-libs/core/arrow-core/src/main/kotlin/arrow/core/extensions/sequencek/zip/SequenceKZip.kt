package arrow.core.extensions.sequencek.zip

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK
import arrow.core.SequenceK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKZip
import kotlin.Deprecated
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val zip_singleton: SequenceKZip = object : arrow.core.extensions.SequenceKZip {}

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
fun <A, B> Kind<ForSequenceK, A>.zip(arg1: Kind<ForSequenceK, B>): SequenceK<Tuple2<A, B>> =
    arrow.core.SequenceK.zip().run {
  this@zip.zip<A, B>(arg1) as arrow.core.SequenceK<arrow.core.Tuple2<A, B>>
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
fun <A, B, C> Kind<ForSequenceK, A>.zipWith(arg1: Kind<ForSequenceK, B>, arg2: Function2<A, B, C>):
    SequenceK<C> = arrow.core.SequenceK.zip().run {
  this@zipWith.zipWith<A, B, C>(arg1, arg2) as arrow.core.SequenceK<C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.zip(): SequenceKZip = zip_singleton
