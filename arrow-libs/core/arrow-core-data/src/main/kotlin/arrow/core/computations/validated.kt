package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Validated
import arrow.core.valid
import kotlin.coroutines.RestrictsSuspension

@Deprecated(
  "The `EitherEffect` computation block supports validated with the right short-circuiting semantics",
  ReplaceWith("EitherEffect", "arrow.core.computations.either.EitherEffect")
)
fun interface ValidatedEffect<E, A> : Effect<Validated<E, A>> {

  suspend fun <B> Validated<E, B>.bind(): B =
    when (this) {
      is Validated.Valid -> a
      is Validated.Invalid -> control().shift(this@bind)
    }

  @Deprecated("This operator is being deprecated due to confusion with Boolean, and unifying a single API. Use bind() instead.", ReplaceWith("bind()"))
  suspend operator fun <B> Validated<E, B>.not(): B = bind()

  @Deprecated("This operator can have problems when you do not capture the value, please use bind() instead", ReplaceWith("bind()"))
  suspend operator fun <B> Validated<E, B>.component1(): B = bind()
}

@Deprecated(
  "The `EitherRestrictedEffect` computation block supports validated with the right short-circuiting semantics",
  ReplaceWith("EitherRestrictedEffect", "arrow.core.computations.either.EitherRestrictedEffect")
)
@RestrictsSuspension
fun interface RestrictedValidatedEffect<E, A> : ValidatedEffect<E, A>

@Suppress("ClassName")
@Deprecated(
  "The `either` computation block supports validated with the right short-circuiting semantics",
  ReplaceWith("either", "arrow.core.computations.either")
)
object validated {
  inline fun <E, A> eager(crossinline c: suspend RestrictedValidatedEffect<E, *>.() -> A): Validated<E, A> =
    Effect.restricted(eff = { RestrictedValidatedEffect { it } }, f = c, just = { it.valid() })

  suspend inline operator fun <E, A> invoke(crossinline c: suspend ValidatedEffect<E, *>.() -> A): Validated<E, A> =
    Effect.suspended(eff = { ValidatedEffect { it } }, f = c, just = { it.valid() })
}
