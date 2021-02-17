package arrow.core.extensions.sequence.zip

import arrow.core.Tuple2
import arrow.core.extensions.SequenceKZip
import kotlin.sequences.Sequence

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
    "this.zip(arg1)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Sequence<A>.zip(arg1: Sequence<B>): Sequence<Tuple2<A, B>> =
  arrow.core.extensions.sequence.zip.Sequence.zip().run {
    arrow.core.SequenceK(this@zip).zip<A, B>(arrow.core.SequenceK(arg1)) as
      kotlin.sequences.Sequence<arrow.core.Tuple2<A, B>>
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
    "this.zip(arg1, arg2)"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Sequence<A>.zipWith(arg1: Sequence<B>, arg2: Function2<A, B, C>): Sequence<C> =
  arrow.core.extensions.sequence.zip.Sequence.zip().run {
    arrow.core.SequenceK(this@zipWith).zipWith<A, B, C>(arrow.core.SequenceK(arg1), arg2) as
      kotlin.sequences.Sequence<C>
  }

/**
 * cached extension
 */
@PublishedApi()
internal val zip_singleton: SequenceKZip = object : arrow.core.extensions.SequenceKZip {}

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
    "Zip typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun zip(): SequenceKZip = zip_singleton
}
