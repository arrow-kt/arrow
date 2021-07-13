package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.None
import arrow.core.Option
import arrow.core.identity
import kotlin.coroutines.RestrictsSuspension

public fun interface OptionEffect<A> : Effect<Option<A>> {
  // TODO conflicts with outer scoped Effect context
  public suspend fun <B> B?.bind(): B =
    this ?: control().shift(None)

  public suspend fun <B> Option<B>.bind(): B =
    fold({ control().shift(None) }, ::identity)
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
