package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Option
import kotlin.coroutines.RestrictsSuspension

fun interface NullableEffect<A> : Effect<A?> {
  suspend fun <B> B?.bind(): B =
    this ?: control().shift(null)

  suspend fun <B> Option<B>.bind(): B =
    orNull().bind()
}

@RestrictsSuspension
fun interface RestrictedNullableEffect<A> : NullableEffect<A>

@Suppress("ClassName")
object nullable {
  inline fun <A> eager(crossinline func: suspend RestrictedNullableEffect<A>.() -> A?): A? =
    Effect.restricted(eff = { RestrictedNullableEffect { it } }, f = func, just = { it })

  suspend inline operator fun <A> invoke(crossinline func: suspend NullableEffect<*>.() -> A?): A? =
    Effect.suspended(eff = { NullableEffect { it } }, f = func, just = { it })
}
