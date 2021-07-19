package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Option
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.RestrictsSuspension

public fun interface NullableEffect<A> : Effect<A?> {
  public suspend fun <B> B?.bind(): B =
    this ?: control().shift(null)

  public suspend fun <B> Option<B>.bind(): B =
    orNull().bind()

  public suspend fun ensure(boolean: Boolean): Unit =
    if (boolean) Unit else control().shift(null)
}

@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so made it top-level.
public suspend fun <B : Any> NullableEffect<*>.ensureNotNull(value: B?): B {
  contract {
    returns() implies (value != null)
  }

  return value ?: control().shift(null)
}

@RestrictsSuspension
public fun interface RestrictedNullableEffect<A> : NullableEffect<A>

@Suppress("ClassName")
public object nullable {
  public inline fun <A> eager(crossinline func: suspend RestrictedNullableEffect<A>.() -> A?): A? =
    Effect.restricted(eff = { RestrictedNullableEffect { it } }, f = func, just = { it })

  public suspend inline operator fun <A> invoke(crossinline func: suspend NullableEffect<*>.() -> A?): A? =
    Effect.suspended(eff = { NullableEffect { it } }, f = func, just = { it })
}
