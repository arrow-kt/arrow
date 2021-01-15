package arrow.core.extensions.sequencek.crosswalk

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKCrosswalk
import arrow.typeclasses.Align
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val crosswalk_singleton: SequenceKCrosswalk = object :
    arrow.core.extensions.SequenceKCrosswalk {}

@JvmName("crosswalk")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "crosswalk(arg0, arg1, arg2)",
  "arrow.core.SequenceK.crosswalk"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> crosswalk(
  arg0: Align<F>,
  arg1: Kind<ForSequenceK, A>,
  arg2: Function1<A, Kind<F, B>>
): Kind<F, Kind<ForSequenceK, B>> = arrow.core.SequenceK
   .crosswalk()
   .crosswalk<F, A, B>(arg0, arg1, arg2) as arrow.Kind<F, arrow.Kind<arrow.core.ForSequenceK, B>>

@JvmName("sequenceL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "sequenceL(arg0, arg1)",
  "arrow.core.SequenceK.sequenceL"
  ),
  DeprecationLevel.WARNING
)
fun <F, A> sequenceL(arg0: Align<F>, arg1: Kind<ForSequenceK, Kind<F, A>>): Kind<F,
    Kind<ForSequenceK, A>> = arrow.core.SequenceK
   .crosswalk()
   .sequenceL<F, A>(arg0, arg1) as arrow.Kind<F, arrow.Kind<arrow.core.ForSequenceK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.crosswalk(): SequenceKCrosswalk = crosswalk_singleton
