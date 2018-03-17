package arrow.typeclasses

import arrow.core.Either
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine

interface Awaitable<A> {
    fun resolve(result: Either<Throwable, A>): Unit

    fun awaitBlocking(): Either<Throwable, A>

    fun awaitNonBlocking(fe: (Throwable) -> Unit, fa: (A) -> Unit): Unit

    suspend fun await(): A = suspendCoroutine { cc ->
        awaitNonBlocking({ cc.resumeWithException(it) }, { cc.resume(it) })
    }
}

interface AwaitableContinuation<A> : Awaitable<A>, Continuation<A>
