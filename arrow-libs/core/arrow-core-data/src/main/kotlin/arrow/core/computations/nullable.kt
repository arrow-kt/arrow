package arrow.core.computations

import arrow.continuations.Effect
import kotlin.coroutines.RestrictsSuspension

fun interface NullableEffect<A> : Effect<A?> {

  @Deprecated("The monadic operator for the Arrow 1.x series will become invoke in 0.13", ReplaceWith("()"))
  suspend fun <B> B?.bind(): B = this()

  @Deprecated("The monadic operator for the Arrow 1.x series will become invoke in 0.13", ReplaceWith("()"))
  suspend operator fun <B> B?.not(): B = this()

  suspend operator fun <B> B?.invoke(): B = this ?: control().shift(null)
}

@RestrictsSuspension
fun interface RestrictedNullableEffect<A> : NullableEffect<A>

@Suppress("ClassName")
object nullable {
  inline fun <A> eager(crossinline func: suspend RestrictedNullableEffect<A>.() -> A?): A? =
    Effect.restricted(eff = { RestrictedNullableEffect { it } }, f = func, just = { it })

  suspend inline operator fun <A> invoke(crossinline func: suspend NullableEffect<*>.() -> A?): A? =
    Effect.suspended(eff = { NullableEffect { it } }, f = func, just = { it })
}
