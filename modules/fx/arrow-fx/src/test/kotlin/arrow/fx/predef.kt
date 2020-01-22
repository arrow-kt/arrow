package arrow.fx

import arrow.Kind
import arrow.core.Either
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.fx.extensions.io.applicativeError.attempt
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.seconds
import arrow.typeclasses.Eq
import kotlinx.atomicfu.atomic
import java.util.concurrent.ExecutorService
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

fun <E, A> EQ(EQA: Eq<A> = Eq.any(), timeout: Duration = 60.seconds): Eq<Kind<IOPartialOf<E>, A>> = Eq { a, b ->
  arrow.core.Option.eq(Either.eq(Eq.any(), Either.eq(Eq.any(), EQA))).run {
    a.attempt().unsafeRunTimed(timeout).eqv(b.attempt().unsafeRunTimed(timeout))
  }
}

fun <A> IO_EQ(EQA: Eq<A> = Eq.any(), timeout: Duration = 60.seconds): Eq<Kind<IOPartialOf<Nothing>, A>> = Eq { a, b ->
  arrow.core.Option.eq(Either.eq(Eq.any(), Either.eq(Eq.any(), EQA))).run {
    a.attempt().unsafeRunTimed(timeout).eqv(b.attempt().unsafeRunTimed(timeout))
  }
}

/**
 * This [ExecutorService] doesn't keep any Thread alive, so the maximumPoolSize should be equal to the # of scheduled tasks.
 *
 * Only useful for very specific task that want to keep track of how many times [kotlin.coroutines.startCoroutine] is called etc,
 * it also names every thread differently so you can rely on the names to check if tasks where scheduled on different Threads.
 */
internal fun newCountingThreadFactory(name: String, maximumPoolSize: Int = 0): ExecutorService =
  ThreadPoolExecutor(0, maximumPoolSize, 0, TimeUnit.MILLISECONDS, SynchronousQueue<Runnable>(), CountingThreadFactory(name))

private class CountingThreadFactory(val name: String) : ThreadFactory {
  private val counter = atomic(0)
  override fun newThread(r: Runnable): Thread =
    Thread(r, "$name-${counter.getAndIncrement()}")
}
