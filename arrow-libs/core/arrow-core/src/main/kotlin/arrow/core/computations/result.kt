package arrow.core.computations

import arrow.continuations.Effect
import kotlin.coroutines.RestrictsSuspension

fun interface ResultEffect<A> : Effect<Result<A>> {

  suspend fun <B> Result<B>.bind(): B = fold(
    onSuccess = { it },
    onFailure = { control().shift(Result.failure(it)) },
  )
}

@RestrictsSuspension
fun interface RestrictedResultEffect<A> : ResultEffect<A>

@Suppress("ClassName")
object result {

  inline fun <A> eager(crossinline c: suspend RestrictedResultEffect<*>.() -> A): Result<A> =
    Effect.restricted(eff = { RestrictedResultEffect { it } }, f = c, just = { Result.success(it) })

  suspend inline operator fun <A> invoke(crossinline c: suspend ResultEffect<*>.() -> A): Result<A> =
    Effect.suspended(eff = { ResultEffect { it } }, f = c, just = { Result.success(it) })

}
