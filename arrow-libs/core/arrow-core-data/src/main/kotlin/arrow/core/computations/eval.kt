package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Eval
import kotlin.coroutines.RestrictsSuspension

fun interface EvalEffect<A> : Effect<Eval<A>> {

  suspend fun <B> Eval<B>.bind(): B =
    value()

  @Deprecated("This operator is being deprecated due to confusion with Boolean, and unifying a single API. Use bind() instead.", ReplaceWith("bind()"))
  suspend operator fun <B> Eval<B>.not(): B =
    bind()

  @Deprecated("This operator can have problems when you do not capture the value, please use bind() instead", ReplaceWith("bind()"))
  suspend operator fun <B> Eval<B>.component1(): B =
    bind()
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
