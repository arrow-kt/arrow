package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Eval
import kotlin.coroutines.RestrictsSuspension

@Deprecated(deprecateInFavorOfEffectScope, ReplaceWith("EffectScope<E>", "arrow.core.continuations.EffectScope"))
public fun interface EvalEffect<A> : Effect<Eval<A>> {
  public suspend fun <B> Eval<B>.bind(): B =
    value()
}

@Deprecated(deprecatedInFavorOfEagerEffectScope, ReplaceWith("EagerEffectScope<E>", "arrow.core.continuations.EagerEffectScope"))
@RestrictsSuspension
public fun interface RestrictedEvalEffect<A> : EvalEffect<A>

@Deprecated(deprecateInFavorOfEffectOrEagerEffect)
@Suppress("ClassName")
public object eval {
  @Deprecated(deprecateInFavorOfEagerEffect, ReplaceWith("eagerEffect(func)", "arrow.core.continuations.eagerEffect"))
  public inline fun <A> eager(crossinline func: suspend RestrictedEvalEffect<A>.() -> A): Eval<A> =
    Effect.restricted(eff = { RestrictedEvalEffect { it } }, f = func, just = Eval.Companion::now)

  @Deprecated(deprecateInFavorOfEffect, ReplaceWith("effect(func)", "arrow.core.continuations.effect"))
  public suspend inline operator fun <A> invoke(crossinline func: suspend EvalEffect<*>.() -> A): Eval<A> =
    Effect.suspended(eff = { EvalEffect { it } }, f = func, just = Eval.Companion::now)
}
