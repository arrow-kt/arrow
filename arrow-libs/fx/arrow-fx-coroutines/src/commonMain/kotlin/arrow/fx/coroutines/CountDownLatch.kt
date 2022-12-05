package arrow.fx.coroutines

import arrow.atomic.Atomic
import arrow.atomic.loop
import kotlinx.coroutines.CompletableDeferred

/**
 * [CountDownLatch] allows for awaiting a given number of countdown signals.
 * Models the behavior of java.util.concurrent.CountDownLatch in Kotlin with `suspend`.
 *
 * Must be initialised with an [initial] value of 1 or higher,
 * if constructed with 0 or negative value then it throws [IllegalArgumentException].
 */
public class CountDownLatch @Throws(IllegalArgumentException::class) constructor(private val initial: Long) {
  private val signal = CompletableDeferred<Unit>()
  private val count = Atomic(initial)
  
  init {
    require(initial > 0) {
      "CountDownLatch must be constructed with positive non-zero initial count, but was $initial"
    }
  }
  
  /** Remaining count */
  public fun count(): Long = count.value
  
  /** Await [count] to reach zero */
  public suspend fun await(): Unit = signal.await()
  
  /** Decrement [count] by one */
  @Suppress("ReturnCount")
  public fun countDown() {
    count.loop { current ->
      when {
        current == 0L -> return
        current == 1L && count.compareAndSet(1L, 0L) -> {
          signal.complete(Unit)
          return
        }
        count.compareAndSet(current, current - 1) -> return
      }
    }
  }
}
