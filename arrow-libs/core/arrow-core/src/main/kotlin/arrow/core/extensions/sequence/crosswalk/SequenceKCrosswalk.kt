package arrow.core.extensions.sequence.crosswalk

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.extensions.SequenceKCrosswalk
import arrow.typeclasses.Align
import kotlin.sequences.Sequence

@JvmName("crosswalk")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with crosswalk, crosswalkMap or crosswalkNull from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <F, A, B> crosswalk(
  arg0: Align<F>,
  arg1: Sequence<A>,
  arg2: Function1<A, Kind<F, B>>
): Kind<F, Kind<ForSequenceK, B>> = arrow.core.extensions.sequence.crosswalk.Sequence
   .crosswalk()
   .crosswalk<F, A, B>(arg0, arrow.core.SequenceK(arg1), arg2) as arrow.Kind<F,
    arrow.Kind<arrow.core.ForSequenceK, B>>

@JvmName("sequenceL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated. Replace with sequenceEither or sequenceValidated from arrow.core.*",
  level = DeprecationLevel.WARNING
)
fun <F, A> sequenceL(arg0: Align<F>, arg1: Sequence<Kind<F, A>>): Kind<F, Kind<ForSequenceK, A>> =
    arrow.core.extensions.sequence.crosswalk.Sequence
   .crosswalk()
   .sequenceL<F, A>(arg0, arrow.core.SequenceK(arg1)) as arrow.Kind<F,
    arrow.Kind<arrow.core.ForSequenceK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val crosswalk_singleton: SequenceKCrosswalk = object :
    arrow.core.extensions.SequenceKCrosswalk {}

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
    "Crosswalk typeclass is deprecated. Use concrete methods on Sequence",
    level = DeprecationLevel.WARNING
  )
  inline fun crosswalk(): SequenceKCrosswalk = crosswalk_singleton}
