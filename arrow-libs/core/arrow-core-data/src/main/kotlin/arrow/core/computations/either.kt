package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Either
import arrow.core.Left
import arrow.core.Validated
import arrow.core.right
import kotlin.coroutines.RestrictsSuspension

fun interface EitherEffect<E, A> : Effect<Either<E, A>> {

  suspend fun <B> Either<E, B>.bind(): B =
    when (this) {
      is Either.Right -> b
      is Either.Left -> control().shift(this@bind)
    }

  suspend fun <B> Validated<E, B>.bind(): B =
    when (this) {
      is Validated.Valid -> a
      is Validated.Invalid -> control().shift(Left(e))
    }

  @Deprecated("This operator is being deprecated due to confusion with Boolean, and unifying a single API. Use bind() instead.", ReplaceWith("bind()"))
  suspend operator fun <B> Either<E, B>.not(): B =
    bind()

  @Deprecated("This operator can have problems when you do not capture the value, please use bind() instead", ReplaceWith("bind()"))
  suspend operator fun <B> Either<E, B>.component1(): B =
    bind()
}

@RestrictsSuspension
fun interface RestrictedEitherEffect<E, A> : EitherEffect<E, A>

@Suppress("ClassName")
object either {
  inline fun <E, A> eager(crossinline c: suspend RestrictedEitherEffect<E, *>.() -> A): Either<E, A> =
    Effect.restricted(eff = { RestrictedEitherEffect { it } }, f = c, just = { it.right() })

  suspend inline operator fun <E, A> invoke(crossinline c: suspend EitherEffect<E, *>.() -> A): Either<E, A> =
    Effect.suspended(eff = { EitherEffect { it } }, f = c, just = { it.right() })
}
