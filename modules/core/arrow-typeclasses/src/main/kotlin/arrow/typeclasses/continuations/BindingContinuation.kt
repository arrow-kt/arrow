package arrow.typeclasses.continuations

import arrow.Kind
import arrow.typeclasses.Monad
import kotlin.coroutines.experimental.RestrictsSuspension

@RestrictsSuspension
interface BindingContinuation<F> : Monad<F> {
    suspend fun <B> bind(m: () -> Kind<F, B>): B

    suspend fun <B> Kind<F, B>.bind(): B = bind { this }

    @Deprecated("Yielding in comprehensions isn't required anymore", ReplaceWith("b"))
    fun <B> yields(b: B): B = b

    @Deprecated("Yielding in comprehensions isn't required anymore", ReplaceWith("b()"))
    fun <B> yields(b: () -> B): B = b()
}