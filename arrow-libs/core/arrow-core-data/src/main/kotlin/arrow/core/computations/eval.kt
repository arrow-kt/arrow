package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Eval
import kotlin.coroutines.RestrictsSuspension

fun interface EvalEffect<A> : Effect<Eval<A>> {

  @Deprecated("The monadic operator for the Arrow 1.x series will become invoke in 0.13", ReplaceWith("()"))
  suspend fun <B> Eval<B>.bind(): B = this()

  @Deprecated("The monadic operator for the Arrow 1.x series will become invoke in 0.13", ReplaceWith("()"))
  suspend operator fun <B> Eval<B>.not(): B = this()

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
