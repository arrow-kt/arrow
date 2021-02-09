package arrow.core.extensions.sequence.unzip

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKUnzip
import kotlin.sequences.Sequence

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
fun <A, B> Sequence<Tuple2<A, B>>.unzip(): Tuple2<Kind<ForSequenceK, A>, Kind<ForSequenceK, B>> =
    arrow.core.extensions.sequence.unzip.Sequence.unzip().run {
  arrow.core.SequenceK(this@unzip).unzip<A, B>() as
    arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK, A>, arrow.Kind<arrow.core.ForSequenceK,
    B>>
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
    "this.unzip { c -> val t = arg1(c);  t.a to t.b }",
    "arrow.core.andThen", "arrow.core.to", "arrow.core.unzip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Sequence<C>.unzipWith(arg1: Function1<C, Tuple2<A, B>>): Tuple2<Kind<ForSequenceK, A>,
    Kind<ForSequenceK, B>> = arrow.core.extensions.sequence.unzip.Sequence.unzip().run {
  arrow.core.SequenceK(this@unzipWith).unzipWith<A, B, C>(arg1) as
    arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK, A>, arrow.Kind<arrow.core.ForSequenceK,
    B>>
}

/**
 * cached extension
 */
@PublishedApi()
internal val unzip_singleton: SequenceKUnzip = object : arrow.core.extensions.SequenceKUnzip {}

@Deprecated(
  "Receiver Sequence object is deprecated, prefer to turn Sequence functions into top-level functions",
  level = DeprecationLevel.WARNING
)
object Sequence {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated(
    "Unzip typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun unzip(): SequenceKUnzip = unzip_singleton
}
