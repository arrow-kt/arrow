package arrow.core.extensions.id.crosswalk

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id.Companion
import arrow.core.extensions.IdCrosswalk
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
internal val crosswalk_singleton: IdCrosswalk = object : arrow.core.extensions.IdCrosswalk {}

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
  "arrow.core.Id.crosswalk"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> crosswalk(
  arg0: Align<F>,
  arg1: Kind<ForId, A>,
  arg2: Function1<A, Kind<F, B>>
): Kind<F, Kind<ForId, B>> = arrow.core.Id
   .crosswalk()
   .crosswalk<F, A, B>(arg0, arg1, arg2) as arrow.Kind<F, arrow.Kind<arrow.core.ForId, B>>

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
  "arrow.core.Id.sequenceL"
  ),
  DeprecationLevel.WARNING
)
fun <F, A> sequenceL(arg0: Align<F>, arg1: Kind<ForId, Kind<F, A>>): Kind<F, Kind<ForId, A>> =
    arrow.core.Id
   .crosswalk()
   .sequenceL<F, A>(arg0, arg1) as arrow.Kind<F, arrow.Kind<arrow.core.ForId, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.crosswalk(): IdCrosswalk = crosswalk_singleton
