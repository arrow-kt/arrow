package arrow.effects

import arrow.Kind
import arrow.core.extensions.either.eq.eq
import arrow.typeclasses.Eq
import java.util.concurrent.ExecutorService
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun <A> EQ(EQA: Eq<A> = Eq.any()): Eq<Kind<ForIO, A>> = Eq { a, b ->
  arrow.core.Either.eq(Eq.any(), EQA).run {
    IO.unsafeRunBlocking(a.fix().attempt()).eqv(IO.unsafeRunBlocking(b.fix().attempt()))
  }
}

fun <A> IOOf<A>.unsafeRunSync(): A =
  IO.unsafeRunBlocking(this)

class CountingThreadFactory(val name: String) : ThreadFactory {
  private val counter = AtomicInteger()
  override fun newThread(r: Runnable): Thread =
    Thread(r, "$name-${counter.getAndIncrement()}")
}

// Creates a ExecutorService that uses every thread only once, so every task is scheduled on a differently numbered Thread.
fun newTestingScheduler(name: String, maximumPoolSize: Int = 50): ExecutorService =
  ThreadPoolExecutor(0, maximumPoolSize, 0, TimeUnit.MILLISECONDS, SynchronousQueue<Runnable>(), CountingThreadFactory(name))
