package arrow.effects

import arrow.core.Either
import arrow.typeclasses.MonadContinuation

suspend inline fun <reified F, A, B> MonadContinuation<F, A>.bindAsync(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> B): B =
        runAsync(AC, f).bind()

suspend inline fun <reified F, A, B> MonadContinuation<F, A>.bindAsyncUnsafe(AC: AsyncContext<F> = asyncContext(), crossinline f: () -> Either<Throwable, B>): B =
        runAsyncUnsafe(AC, f).bind()
