package arrow.typeclasses.internal

import arrow.core.Either
import arrow.core.Tuple2
import arrow.core.toT
import arrow.typeclasses.Awaitable
import java.util.concurrent.CountDownLatch

object Platform {
    fun <A> awaitableLatch(): Awaitable<A> = object : Awaitable<A> {
        val latch = CountDownLatch(1)

        var result: Either<Throwable, A>? = null

        var callbacks: Tuple2<(Throwable) -> Unit, (A) -> Unit>? = null

        override fun awaitNonBlocking(fe: (Throwable) -> Unit, fa: (A) -> Unit) {
            synchronized(this) {
                if (this.callbacks == null) {
                    callbacks = fe toT fa
                    result?.fold(fe, fa)
                } else {
                    fe(RuntimeException("Awaiting nonblocking twice"))
                }
            }
        }

        override fun resolve(result: Either<Throwable, A>) {
            synchronized(this) {
                if (this.result == null) {
                    this.result = result
                    val callbacksLocal: Tuple2<(Throwable) -> Unit, (A) -> Unit>? = this.callbacks
                    if (callbacksLocal != null) {
                        result.fold(callbacksLocal.a, callbacksLocal.b)
                    }
                }
                latch.countDown()
            }
        }

        override fun awaitBlocking(): Either<Throwable, A> {
            latch.await()
            return result!!
        }
    }
}
