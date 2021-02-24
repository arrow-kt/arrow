package arrow.core.extensions.validated.semigroupK

import arrow.Kind
import arrow.core.ForValidated
import arrow.core.Validated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <E, A> Kind<Kind<ForValidated, E>, A>.combineK(
  SE: Semigroup<E>,
  arg1: Kind<Kind<ForValidated, E>, A>
): Validated<E, A> = arrow.core.Validated.semigroupK<E>(SE).run {
  this@combineK.combineK<A>(arg1) as arrow.core.Validated<E, A>
}

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <E, A> algebra(SE: Semigroup<E>): Semigroup<Kind<Kind<ForValidated, E>, A>> =
  arrow.core.Validated
    .semigroupK<E>(SE)
    .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.Kind<arrow.core.ForValidated, E>,
      A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
inline fun <E> Companion.semigroupK(SE: Semigroup<E>): ValidatedSemigroupK<E> = object :
  arrow.core.extensions.ValidatedSemigroupK<E> {
  override fun SE(): arrow.typeclasses.Semigroup<E> =
    SE
}
