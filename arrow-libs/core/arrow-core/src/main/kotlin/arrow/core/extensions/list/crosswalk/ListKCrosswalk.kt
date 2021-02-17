package arrow.core.extensions.list.crosswalk

import arrow.Kind
import arrow.core.ForListK
import arrow.core.extensions.ListKCrosswalk
import arrow.typeclasses.Align
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

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
  arg1: List<A>,
  arg2: Function1<A, Kind<F, B>>
): Kind<F, Kind<ForListK, B>> = arrow.core.extensions.list.crosswalk.List
  .crosswalk()
  .crosswalk<F, A, B>(arg0, arrow.core.ListK(arg1), arg2) as arrow.Kind<F,
  arrow.Kind<arrow.core.ForListK, B>>

@JvmName("sequenceL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with sequenceValidated or sequenceEither from arrow.core.*")
fun <F, A> sequenceL(arg0: Align<F>, arg1: List<Kind<F, A>>): Kind<F, Kind<ForListK, A>> =
  arrow.core.extensions.list.crosswalk.List
    .crosswalk()
    .sequenceL<F, A>(arg0, arrow.core.ListK(arg1)) as arrow.Kind<F, arrow.Kind<arrow.core.ForListK,
      A>>

/**
 * cached extension
 */
@PublishedApi()
internal val crosswalk_singleton: ListKCrosswalk = object : arrow.core.extensions.ListKCrosswalk {}

@Deprecated("Receiver List object is deprecated, prefer to turn List functions into top-level functions")
object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("Crosswalk typeclasses is deprecated. Use concrete methods on List")
  inline fun crosswalk(): ListKCrosswalk = crosswalk_singleton
}
