package arrow.core.extensions.either.semigroupK

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.extensions.EitherSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.Any
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: EitherSemigroupK<Any?> = object : EitherSemigroupK<Any?> {}

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <L, A> Kind<Kind<ForEither, L>, A>.combineK(arg1: Kind<Kind<ForEither, L>, A>): Either<L, A> =
  arrow.core.Either.semigroupK<L>().run {
    this@combineK.combineK<A>(arg1) as arrow.core.Either<L, A>
  }

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
fun <L, A> algebra(): Semigroup<Kind<Kind<ForEither, L>, A>> = arrow.core.Either
  .semigroupK<L>()
  .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.Kind<arrow.core.ForEither, L>, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Kind/type constructors will be deprecated, so this typeclass will no longer be available from 0.13.0")
inline fun <L> Companion.semigroupK(): EitherSemigroupK<L> = semigroupK_singleton as
  arrow.core.extensions.EitherSemigroupK<L>
