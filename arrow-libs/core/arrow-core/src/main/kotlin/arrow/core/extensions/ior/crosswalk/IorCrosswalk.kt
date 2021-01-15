package arrow.core.extensions.ior.crosswalk

import arrow.Kind
import arrow.core.ForIor
import arrow.core.Ior.Companion
import arrow.core.extensions.IorCrosswalk
import arrow.typeclasses.Align
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val crosswalk_singleton: IorCrosswalk<Any?> = object : IorCrosswalk<Any?> {}

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
  "arrow.core.Ior.crosswalk"
  ),
  DeprecationLevel.WARNING
)
fun <L, F, A, B> crosswalk(
  arg0: Align<F>,
  arg1: Kind<Kind<ForIor, L>, A>,
  arg2: Function1<A, Kind<F, B>>
): Kind<F, Kind<Kind<ForIor, L>, B>> = arrow.core.Ior
   .crosswalk<L>()
   .crosswalk<F, A, B>(arg0, arg1, arg2) as arrow.Kind<F, arrow.Kind<arrow.Kind<arrow.core.ForIor,
    L>, B>>

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
  "arrow.core.Ior.sequenceL"
  ),
  DeprecationLevel.WARNING
)
fun <L, F, A> sequenceL(arg0: Align<F>, arg1: Kind<Kind<ForIor, L>, Kind<F, A>>): Kind<F,
    Kind<Kind<ForIor, L>, A>> = arrow.core.Ior
   .crosswalk<L>()
   .sequenceL<F, A>(arg0, arg1) as arrow.Kind<F, arrow.Kind<arrow.Kind<arrow.core.ForIor, L>, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <L> Companion.crosswalk(): IorCrosswalk<L> = crosswalk_singleton as
    arrow.core.extensions.IorCrosswalk<L>
