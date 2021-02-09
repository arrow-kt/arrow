package arrow.core.extensions.sequencek.unzip

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKUnzip

/**
 * cached extension
 */
@PublishedApi()
internal val unzip_singleton: SequenceKUnzip = object : arrow.core.extensions.SequenceKUnzip {}

@JvmName("unzip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.unzip()",
    "arrow.core.unzip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForSequenceK, Tuple2<A, B>>.unzip(): Tuple2<Kind<ForSequenceK, A>,
    Kind<ForSequenceK, B>> = arrow.core.SequenceK.unzip().run {
  this@unzip.unzip<A, B>() as arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK, A>,
    arrow.Kind<arrow.core.ForSequenceK, B>>
}

@JvmName("unzipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.unzip(arg1.andThen { t -> t.a to t.b })",
    "arrow.core.andThen", "arrow.core.to", "arrow.core.unzip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForSequenceK, C>.unzipWith(arg1: Function1<C, Tuple2<A, B>>):
    Tuple2<Kind<ForSequenceK, A>, Kind<ForSequenceK, B>> = arrow.core.SequenceK.unzip().run {
  this@unzipWith.unzipWith<A, B, C>(arg1) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK,
    A>, arrow.Kind<arrow.core.ForSequenceK, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Unzip typeclass is deprecated. Use concrete methods on Sequence",
  level = DeprecationLevel.WARNING
)
inline fun Companion.unzip(): SequenceKUnzip = unzip_singleton
