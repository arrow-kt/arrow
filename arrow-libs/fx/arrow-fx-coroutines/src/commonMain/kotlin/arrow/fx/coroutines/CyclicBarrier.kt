package arrow.fx.coroutines

import arrow.atomic.Atomic
import arrow.atomic.loop
import arrow.atomic.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred

/**
 * A [CyclicBarrier] is a synchronization mechanism that allows a set of coroutines to wait for each other
 * to reach a certain point before continuing execution.
 * It is called a "cyclic" barrier because it can be reused after all coroutines have reached the barrier and released.
 *
 * To use a CyclicBarrier, each coroutine must call the [await] method on the barrier object,
 * which will cause the coroutine to suspend until the required number of coroutines have reached the barrier.
 * Once all coroutines have reached the barrier they will _resume_ execution.
 *
 * Models the behavior of java.util.concurrent.CyclicBarrier in Kotlin with `suspend`.
 */
public class CyclicBarrier(public val capacity: Int) {
  init {
    require(capacity > 0) {
      "Cyclic barrier must be constructed with positive non-zero capacity $capacity but was $capacity > 0"
    }
  }
  
  private data class State(val awaiting: Int, val epoch: Long, val unblock: CompletableDeferred<Unit>)
  
  private val state: Atomic<State> = Atomic(State(capacity, 0, CompletableDeferred()))
  
  /**
   * When [await] is called the function will suspend until the required number of coroutines have reached the barrier.
   * Once the [capacity] of the barrier has been reached, the coroutine will be released and continue execution.
   */
  public suspend fun await() {
    state.loop { original ->
      val (awaiting, epoch, unblock) = original
      val awaitingNow = awaiting - 1
      if (awaitingNow == 0 && state.compareAndSet(original, State(capacity, epoch + 1, CompletableDeferred()))) {
        unblock.complete(Unit)
        return
      } else if (state.compareAndSet(original, State(awaitingNow, epoch, unblock))) {
        return try {
          unblock.await()
        } catch (cancelled: CancellationException) {
          state.update { s -> if (s.epoch == epoch) s.copy(awaiting = s.awaiting + 1) else s }
          throw cancelled
        }
      }
    }
  }
}
