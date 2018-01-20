package arrow.effects

import arrow.core.Either
import arrow.typeclasses.MonadContinuation

suspend inline fun <reified F, A, B> MonadContinuation<F, A>.bindAsync(AC: Async<F> = async(), noinline f: () -> B): B =
        AC(f).bind()

suspend inline fun <reified F, A, B> MonadContinuation<F, A>.bindAsyncUnsafe(AC: Async<F> = async(), noinline f: () -> Either<Throwable, B>): B =
        AC.invokeUnsafe(f).bind()
