package arrow.effects.internal

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.effects.Duration
import arrow.effects.IO
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.AbstractQueuedSynchronizer

object Platform {

    class ArrayStack<A> : ArrayDeque<A>()

    inline fun <A> onceOnly(crossinline f: (A) -> Unit): (A) -> Unit {
        val wasCalled = AtomicBoolean(false)

        return { a ->
            if (!wasCalled.getAndSet(true)) {
                f(a)
            }
        }
    }

    fun <A> unsafeResync(ioa: IO<A>, limit: Duration): Option<A> {
        val latch = OneShotLatch()
        var ref: Either<Throwable, A>? = null
        ioa.unsafeRunAsync { a ->
            ref = a
            latch.releaseShared(1)
        }

        if (limit == Duration.INFINITE) {
            latch.acquireSharedInterruptibly(1)
        } else {
            latch.tryAcquireSharedNanos(1, limit.nanoseconds)
        }

        val eitherRef = ref

        return when (eitherRef) {
            null -> None
            is Either.Left -> throw eitherRef.a
            is Either.Right -> Some(eitherRef.b)
        }
    }
}

private class OneShotLatch : AbstractQueuedSynchronizer() {
    override fun tryAcquireShared(ignored: Int): Int =
            if (state != 0) {
                1
            } else {
                -1
            }

    override fun tryReleaseShared(ignore: Int): Boolean {
        state = 1
        return true
    }
}
