package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Const
import arrow.core.const
import kotlin.coroutines.RestrictsSuspension

@Deprecated(
  "Const binding does not require suspension and this computation block will be removed."
)
fun interface ConstEffect<A, T> : Effect<Const<A, T>> {

  @Deprecated("The monadic operator for the Arrow 1.x series will become invoke in 0.13", ReplaceWith("()"))
  suspend fun <B> Const<A, B>.bind(): B = this()

  @Deprecated("The monadic operator for the Arrow 1.x series will become invoke in 0.13", ReplaceWith("()"))
  suspend operator fun <B> Const<A, B>.not(): B = this()

  suspend operator fun <B> Const<A, B>.invoke(): B =
    value() as B
}

@Deprecated(
  "Const binding does not require suspension and this computation block will be removed."
)
@RestrictsSuspension
fun interface RestrictedConstEffect<E, A> : ConstEffect<E, A>

@Suppress("ClassName")
@Deprecated(
  "Const binding does not require suspension and this computation block will be removed."
)
object const {
  inline fun <A, T> eager(crossinline c: suspend RestrictedConstEffect<A, *>.() -> A): Const<A, T> =
    Effect.restricted(eff = { RestrictedConstEffect { it } }, f = c, just = { it.const() })

  suspend inline operator fun <A, T> invoke(crossinline c: suspend ConstEffect<A, *>.() -> A): Const<A, T> =
    Effect.suspended(eff = { ConstEffect { it } }, f = c, just = { it.const() })
}
