package arrow.fx.coroutines

import arrow.core.continuations.AtomicRef
import arrow.core.continuations.loop
import arrow.core.continuations.update
import arrow.core.continuations.updateAndGet
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

  private data class State(
    /** The number of parties that are still required to join before the barrier will open and reset. **/
    val awaiting: Int,
    val epoch: Long,
    val reset: Boolean,
    val unblock: CompletableDeferred<Unit>,
    val resetComplete: CompletableDeferred<Unit>
  )

  private val state: AtomicRef<State> =
    AtomicRef(State(capacity, 0, false, CompletableDeferred(), CompletableDeferred()))

  /** The number of parties currently waiting. **/
  public val numberWaiting: Int
    get() = capacity - state.get().awaiting

  /**
   * When called, all waiting coroutines will be cancelled with [CancellationException].
   * When all threads have been cancelled the barrier will cycle.
   */
  public suspend fun reset() {
    state.updateAndGet { s -> s.copy(reset = true) }
      .resetComplete.await()

    compareAndCycleState(state.get())
  }

  private fun compareAndCycleState(original: State) = state
    .compareAndSet(
      original,
      State(capacity, original.epoch + 1, false, CompletableDeferred(), CompletableDeferred())
    )

  /**
   * When [await] is called the function will suspend until the required number of coroutines have reached the barrier.
   * Once the [capacity] of the barrier has been reached, the coroutine will be released and continue execution.
   */
  public suspend fun await() {
    state.loop { original ->
      val (awaiting, epoch, reset, unblock, resetComplete) = original
      val awaitingNow = awaiting - 1
      when {
        reset -> {
          if (awaiting == capacity) resetComplete.complete(Unit)
          throw CancellationException("CyclicBarrier was reset.")
        }
        awaitingNow == 0 && compareAndCycleState(original) -> {
          unblock.complete(Unit)
          return
        }
        state.compareAndSet(original, original.copy(awaiting = awaitingNow)) -> {
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
}
