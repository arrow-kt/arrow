package arrow.fx.coroutines

import arrow.core.continuations.AtomicRef
import arrow.core.continuations.loop
import arrow.core.continuations.update
import arrow.core.nonFatalOrThrow
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

  private sealed interface State {
    val epoch: Long
  }

  private data class Awaiting(
    /** Current number of waiting parties. **/
    val awaitingNow: Int,
    override val epoch: Long,
    val unblock: CompletableDeferred<Unit>
  ) : State

  private data class Resetting(
    val awaitingNow: Int,
    override val epoch: Long,
    /** Barrier used to ensure all awaiting threads are ready to reset. **/
    val unblock: CompletableDeferred<Unit>
  ) : State

  private val state: AtomicRef<State> = AtomicRef(Awaiting(capacity, 0, CompletableDeferred()))

  /**
   * When called, all waiting coroutines will be cancelled with [CancellationException].
   * When all coroutines have been cancelled the barrier will cycle.
   */
  public suspend fun reset() {
    when (val original = state.get()) {
      is Awaiting -> {
        val resetBarrier = CompletableDeferred<Unit>()
        if (state.compareAndSet(original, Resetting(original.awaitingNow, original.epoch, resetBarrier))) {
          original.unblock.cancel(CyclicBarrierCancellationException())
          resetBarrier.await()
        } else reset()
      }

      // We're already resetting, await all waiters to finish
      is Resetting -> original.unblock.await()
    }
  }

  private fun attemptBarrierAction(unblock: CompletableDeferred<Unit>) {
    try {
      barrierAction.invoke()
    } catch (e: Throwable) {
      val cancellationException =
        if (e is CancellationException) e
        else CancellationException("CyclicBarrier barrierAction failed with exception.", e.nonFatalOrThrow())
      unblock.cancel(cancellationException)
      throw cancellationException
    }
  }

  /**
   * When [await] is called the function will suspend until the required number of coroutines have called [await].
   * Once the [capacity] of the barrier has been reached, the coroutine will be released and continue execution.
   */
  public suspend fun await() {
    state.loop { state ->
      when (state) {
        is Awaiting -> {
          val (awaiting, epoch, unblock) = state
          val awaitingNow = awaiting - 1
          if (awaitingNow == 0 && this.state.compareAndSet(
              state,
              Awaiting(capacity, epoch + 1, CompletableDeferred())
            )
          ) {
            attemptBarrierAction(unblock)
            unblock.complete(Unit)
            return
          } else if (this.state.compareAndSet(state, Awaiting(awaitingNow, epoch, unblock))) {
            return try {
              unblock.await()
            } catch (c: CyclicBarrierCancellationException) {
              countdown(state, c)
              throw c
            } catch (cancelled: CancellationException) {
              this.state.update { s ->
                when {
                  s is Awaiting && s.epoch == epoch -> s.copy(awaitingNow = s.awaitingNow + 1)
                  else -> s
                }
              }
              throw cancelled

            }
          }
        }

        is Resetting -> {
          state.unblock.await()
          // State resets to `Awaiting` after `reset.unblock`.
          // Unless there is another racing reset, it will be in `Awaiting` in next loop.
          await()
        }
      }
    }
  }

  private fun countdown(original: Awaiting, ex: CyclicBarrierCancellationException): Boolean {
    state.loop { state ->
      when (state) {
        is Resetting -> {
          val awaitingNow = state.awaitingNow + 1
          if (awaitingNow < capacity && this.state.compareAndSet(state, state.copy(awaitingNow = awaitingNow))) {
            return false
          } else if (awaitingNow == capacity && this.state.compareAndSet(
              state, Awaiting(capacity, state.epoch + 1, CompletableDeferred())
            )
          ) {
            return state.unblock.complete(Unit)
          } else countdown(original, ex)
        }

        is Awaiting -> throw IllegalStateException("Awaiting appeared during resetting.")
      }
    }
  }

}

public class CyclicBarrierCancellationException : CancellationException("CyclicBarrier was cancelled")
