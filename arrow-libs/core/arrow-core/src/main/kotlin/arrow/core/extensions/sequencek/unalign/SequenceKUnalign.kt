package arrow.core.extensions.sequencek.unalign

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.Ior
import arrow.core.SequenceK.Companion
import arrow.core.Tuple2
import arrow.core.extensions.SequenceKUnalign
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val unalign_singleton: SequenceKUnalign = object : arrow.core.extensions.SequenceKUnalign
    {}

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
  "unalign(arg0)",
  "arrow.core.SequenceK.unalign"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> unalign(arg0: Kind<ForSequenceK, Ior<A, B>>): Tuple2<Kind<ForSequenceK, A>,
    Kind<ForSequenceK, B>> = arrow.core.SequenceK
   .unalign()
   .unalign<A, B>(arg0) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK, A>,
    arrow.Kind<arrow.core.ForSequenceK, B>>

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
  "unalignWith(arg0, arg1)",
  "arrow.core.SequenceK.unalignWith"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> unalignWith(arg0: Kind<ForSequenceK, C>, arg1: Function1<C, Ior<A, B>>):
    Tuple2<Kind<ForSequenceK, A>, Kind<ForSequenceK, B>> = arrow.core.SequenceK
   .unalign()
   .unalignWith<A, B, C>(arg0, arg1) as arrow.core.Tuple2<arrow.Kind<arrow.core.ForSequenceK, A>,
    arrow.Kind<arrow.core.ForSequenceK, B>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.unalign(): SequenceKUnalign = unalign_singleton
