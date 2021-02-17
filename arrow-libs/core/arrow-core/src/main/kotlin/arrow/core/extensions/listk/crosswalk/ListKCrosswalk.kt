package arrow.core.extensions.listk.crosswalk

import arrow.Kind
import arrow.core.ForListK
import arrow.core.ListK.Companion
import arrow.core.extensions.ListKCrosswalk
import arrow.typeclasses.Align
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val crosswalk_singleton: ListKCrosswalk = object : arrow.core.extensions.ListKCrosswalk {}

@JvmName("crosswalk")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with crosswalk, crosswalkMap or crosswalkNull from arrow.core.*")
fun <F, A, B> crosswalk(
  arg0: Align<F>,
  arg1: Kind<ForListK, A>,
  arg2: Function1<A, Kind<F, B>>
): Kind<F, Kind<ForListK, B>> = arrow.core.ListK
  .crosswalk()
  .crosswalk<F, A, B>(arg0, arg1, arg2) as arrow.Kind<F, arrow.Kind<arrow.core.ForListK, B>>

@JvmName("sequenceL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequenceValidated or sequenceEither from arrow.core.*")
fun <F, A> sequenceL(arg0: Align<F>, arg1: Kind<ForListK, Kind<F, A>>): Kind<F, Kind<ForListK, A>> =
  arrow.core.ListK
    .crosswalk()
    .sequenceL<F, A>(arg0, arg1) as arrow.Kind<F, arrow.Kind<arrow.core.ForListK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Crosswalk typeclasses is deprecated. Use concrete methods on List")
inline fun Companion.crosswalk(): ListKCrosswalk = crosswalk_singleton
