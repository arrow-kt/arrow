package arrow.core.extensions.sequence.unalign

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Ior
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKUnalign
import kotlin.sequences.Sequence

@JvmName("unalign")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg0.unalign()",
    "arrow.core.unalign"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> unalign(arg0: Sequence<Ior<A, B>>): Tuple2<Kind<ForSequenceK, A>, Kind<ForSequenceK, B>> =
  arrow.core.extensions.sequence.unalign.Sequence
    .unalign()
    .unalign<A, B>(arrow.core.SequenceK(arg0)) as
    arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK, A>, arrow.Kind<arrow.core.ForSequenceK, B>>

@JvmName("unalignWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg0.unalign(arg1)",
    "arrow.core.unalign"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> unalignWith(arg0: Sequence<C>, arg1: Function1<C, Ior<A, B>>):
  Tuple2<Kind<ForSequenceK, A>, Kind<ForSequenceK, B>> =
    arrow.core.extensions.sequence.unalign.Sequence
      .unalign()
      .unalignWith<A, B, C>(arrow.core.SequenceK(arg0), arg1) as
      arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK, A>, arrow.Kind<arrow.core.ForSequenceK,
          B>>

/**
 * cached extension
 */
@PublishedApi()
internal val unalign_singleton: SequenceKUnalign = object : arrow.core.extensions.SequenceKUnalign
{}

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
    "Unalign typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun unalign(): SequenceKUnalign = unalign_singleton
}
