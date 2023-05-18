package arrow.fx.coroutines

import arrow.core.continuations.AtomicRef
import arrow.core.continuations.loop
import arrow.core.continuations.update
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
 *
 * @param capacity The number of coroutines that must await until the barrier cycles and all are released.
 * @param barrierAction An optional runnable that will be executed when the barrier is cycled, but before releasing.
 */
public class CyclicBarrier(public val capacity: Int, private val barrierAction: () -> Unit = {}) {
  init {
    require(capacity > 0) {
      "Cyclic barrier must be constructed with positive non-zero capacity $capacity but was $capacity > 0"
    }
  }

  private data class State(
    /** The number of parties that are still required to join before the barrier will cycle. **/
    val awaiting: Int,
    val epoch: Long,
    /** Barrier used to ensure all awaiting threads are ready to reset. **/
    val resetInProgress: CyclicBarrier?,
    val unblock: CompletableDeferred<Unit>
  )

  private val state: AtomicRef<State> = AtomicRef(State(capacity, 0, null, CompletableDeferred()))

  /** The number of parties currently waiting. **/
  public val numberWaiting: Int
    get() = capacity - state.get().awaiting

  /**
   * When called, all waiting coroutines will be cancelled with [CancellationException].
   * When all threads have been cancelled the barrier will cycle.
   */
  public fun reset(): Unit = if (numberWaiting == 0) {
    setNextState()
  } else {
    state.update { s ->
      s.copy(
        resetInProgress = CyclicBarrier(capacity = numberWaiting, barrierAction = { this.setNextState() })
      )
    }
    state.get().unblock.cancel()
  }

  private fun nextState(currentEpoch: Long) = State(
    awaiting = capacity,
    epoch = currentEpoch + 1,
    resetInProgress = null,
    unblock = CompletableDeferred()
  )

  private fun setNextState() = state.update { s -> nextState(s.epoch) }

  /**
   * When [await] is called the function will suspend until the required number of coroutines have reached the barrier.
   * Once the [capacity] of the barrier has been reached, the coroutine will be released and continue execution.
   */
  public suspend fun await() {
    state.loop { original ->
      val (awaiting, epoch, resetInProgress, unblock) = original
      val awaitingNow = awaiting - 1
      when {
        resetInProgress != null -> {
          resetInProgress.await()
          throw CancellationException("CyclicBarrier was reset.")
        }
        awaitingNow == 0 && state.compareAndSet(original, nextState(epoch)) -> {
          barrierAction.invoke()
          unblock.complete(Unit)
          return
        }
        state.compareAndSet(original, original.copy(awaiting = awaitingNow)) -> {
          return try {
            unblock.await()
          } catch (cancelled: CancellationException) {
            val resetInProgress = state.get().resetInProgress
            if (resetInProgress != null) {
              resetInProgress.await()
              throw CancellationException("CyclicBarrier was reset.")
            } else {
              state.update { s -> if (s.epoch == epoch) s.copy(awaiting = s.awaiting + 1) else s }
              throw cancelled
            }
          }
        }
      }
    }
  }
}
