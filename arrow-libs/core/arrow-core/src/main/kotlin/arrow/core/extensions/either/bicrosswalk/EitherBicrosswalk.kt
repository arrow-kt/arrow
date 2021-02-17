package arrow.core.extensions.either.bicrosswalk

import arrow.Kind
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.extensions.EitherBicrosswalk
import arrow.typeclasses.Align
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val bicrosswalk_singleton: EitherBicrosswalk = object :
  arrow.core.extensions.EitherBicrosswalk {}

@JvmName("bicrosswalk")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with bitraverse or bitraverseValidated from arrow.core.*")
fun <F, A, B, C, D> bicrosswalk(
  arg0: Align<F>,
  arg1: Kind<Kind<ForEither, A>, B>,
  arg2: Function1<A, Kind<F, C>>,
  arg3: Function1<B, Kind<F, D>>
): Kind<F, Kind<Kind<ForEither, C>, D>> = arrow.core.Either
  .bicrosswalk()
  .bicrosswalk<F, A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.Kind<F,
  arrow.Kind<arrow.Kind<arrow.core.ForEither, C>, D>>

@JvmName("bisequenceL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension kinded projected functions are deprecated. Replace with bisequence or bisequenceValidated from arrow.core.*")
fun <F, A, B> bisequenceL(arg0: Align<F>, arg1: Kind<Kind<ForEither, Kind<F, A>>, Kind<F, B>>):
  Kind<F, Kind<Kind<ForEither, A>, B>> = arrow.core.Either
    .bicrosswalk()
    .bisequenceL<F, A, B>(arg0, arg1) as arrow.Kind<F, arrow.Kind<arrow.Kind<arrow.core.ForEither,
        A>, B>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Bicrosswalk typeclasses is deprecated. Use concrete methods on Either")
inline fun Companion.bicrosswalk(): EitherBicrosswalk = bicrosswalk_singleton
