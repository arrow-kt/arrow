package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Eval
import kotlin.coroutines.RestrictsSuspension

fun interface EvalEffect<A> : Effect<Eval<A>> {
  suspend operator fun <B> Eval<B>.invoke(): B =
    value()
}

@RestrictsSuspension
fun interface RestrictedEvalEffect<A> : EvalEffect<A>

@Suppress("ClassName")
object eval {
  inline fun <A> eager(crossinline func: suspend RestrictedEvalEffect<A>.() -> A): Eval<A> =
    Effect.restricted(eff = { RestrictedEvalEffect { it } }, f = func, just = Eval.Companion::just)

  suspend inline operator fun <A> invoke(crossinline func: suspend EvalEffect<*>.() -> A): Eval<A> =
    Effect.suspended(eff = { EvalEffect { it } }, f = func, just = Eval.Companion::just)
}
