package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Eval
import kotlin.coroutines.RestrictsSuspension

@Deprecated("EvalEffect is redundant. Use Eval#value directly instead")
public fun interface EvalEffect<A> : Effect<Eval<A>> {
  @Deprecated(
    "EvalEffect is redundant. Use Eval#value directly instead",
    ReplaceWith("this.value()")
  )
  public suspend fun <B> Eval<B>.bind(): B =
    value()
}

@Deprecated("RestrictedEvalEffect is redundant. Use Eval#value directly instead")
@RestrictsSuspension
public fun interface RestrictedEvalEffect<A> : EvalEffect<A>

@Deprecated("EvalEffect is redundant. Use Eval#value directly instead")
@Suppress("ClassName")
public object eval {
  @Deprecated("EvalEffect is redundant. Use Eval#value directly instead")
  public inline fun <A> eager(crossinline func: suspend RestrictedEvalEffect<A>.() -> A): Eval<A> =
    Effect.restricted(eff = { RestrictedEvalEffect { it } }, f = func, just = Eval.Companion::now)
  
  @Deprecated("EvalEffect is redundant. Use Eval#value) directly instead")
  public suspend inline operator fun <A> invoke(crossinline func: suspend EvalEffect<*>.() -> A): Eval<A> =
    Effect.suspended(eff = { EvalEffect { it } }, f = func, just = Eval.Companion::now)
}
