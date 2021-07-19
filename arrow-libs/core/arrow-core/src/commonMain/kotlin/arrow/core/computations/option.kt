package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.None
import arrow.core.Option
import arrow.core.identity
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.RestrictsSuspension

public fun interface OptionEffect<A> : Effect<Option<A>> {
  @Deprecated("Conflicts with other bind syntax from outer scopes, use ensureNotNull instead.", ReplaceWith("ensureNotNull(this)"))
  public suspend fun <B> B?.bind(): B =
    this ?: control().shift(None)

  public suspend fun <B> Option<B>.bind(): B =
    fold({ control().shift(None) }, ::identity)

  public suspend fun ensure(boolean: Boolean): Unit =
    if (boolean) Unit else control().shift(None)
}

@OptIn(ExperimentalContracts::class) // Contracts not available on open functions, so made it top-level.
public suspend fun <B : Any> OptionEffect<*>.ensureNotNull(value: B?): B {
  contract {
    returns() implies (value != null)
  }

  return value ?: (this as OptionEffect<Any?>).control().shift(None)
}
@RestrictsSuspension
public fun interface RestrictedOptionEffect<A> : OptionEffect<A>

@Suppress("ClassName")
public object option {
  public inline fun <A> eager(crossinline func: suspend RestrictedOptionEffect<A>.() -> A): Option<A> =
    Effect.restricted(eff = { RestrictedOptionEffect { it } }, f = func, just = { Option.fromNullable(it) })

  public suspend inline operator fun <A> invoke(crossinline func: suspend OptionEffect<*>.() -> A?): Option<A> =
    Effect.suspended(eff = { OptionEffect { it } }, f = func, just = { Option.fromNullable(it) })
}
