package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Eval
import kotlin.coroutines.RestrictsSuspension

public fun interface EvalEffect<A> : Effect<Eval<A>> {

  public suspend fun <B> Eval<B>.bind(): B =
    value()
}

@RestrictsSuspension
public fun interface RestrictedEvalEffect<A> : EvalEffect<A>

@Suppress("ClassName")
public object eval {
  public inline fun <A> eager(crossinline func: suspend RestrictedEvalEffect<A>.() -> A): Eval<A> =
    Effect.restricted(eff = { RestrictedEvalEffect { it } }, f = func, just = Eval.Companion::now)

  public suspend inline operator fun <A> invoke(crossinline func: suspend EvalEffect<*>.() -> A): Eval<A> =
    Effect.suspended(eff = { EvalEffect { it } }, f = func, just = Eval.Companion::now)
}
