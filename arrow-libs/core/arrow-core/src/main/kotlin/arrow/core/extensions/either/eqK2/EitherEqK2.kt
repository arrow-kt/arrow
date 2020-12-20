package arrow.core.extensions.either.eqK2

import arrow.Kind
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.extensions.EitherEqK2
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val eqK2_singleton: EitherEqK2 = object : arrow.core.extensions.EitherEqK2 {}

@JvmName("eqK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <A, B> Kind<Kind<ForEither, A>, B>.eqK(
  arg1: Kind<Kind<ForEither, A>, B>,
  arg2: Eq<A>,
  arg3: Eq<B>
): Boolean = arrow.core.Either.eqK2().run {
  this@eqK.eqK<A, B>(arg1, arg2, arg3) as kotlin.Boolean
}

@JvmName("liftEq")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <A, B> liftEq(arg0: Eq<A>, arg1: Eq<B>): Eq<Kind<Kind<ForEither, A>, B>> = arrow.core.Either
  .eqK2()
  .liftEq<A, B>(arg0, arg1) as arrow.typeclasses.Eq<arrow.Kind<arrow.Kind<arrow.core.ForEither, A>,
  B>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
inline fun Companion.eqK2(): EitherEqK2 = eqK2_singleton
