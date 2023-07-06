package arrow.fx.coroutines

import arrow.core.continuations.AtomicRef
import arrow.core.continuations.loop
import arrow.core.continuations.update
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin

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
 */
public class CyclicBarrier(public val capacity: Int) {
  init {
    require(capacity > 0) {
      "Cyclic barrier must be constructed with positive non-zero capacity $capacity but was $capacity > 0"
    }
  }

  private sealed interface State

  private data class Awaiting(
    val awaiting: Int, val epoch: Long, val unblock: CompletableDeferred<Unit>
  ) : State

  private data class Resetting(
    val awaitingNow: Int, val unblock: CompletableDeferred<Unit>
  ) : State

  private val state: AtomicRef<State> = AtomicRef(Awaiting(capacity, 0, CompletableDeferred()))

  public suspend fun reset() {
    when (val original = state.get()) {
      is Awaiting -> {
        val resetBarrier = CompletableDeferred<Unit>()
        if (state.compareAndSet(
            original, Resetting(original.awaiting, resetBarrier)
          )
        ) {
          original.unblock.cancel(CyclicBarrierCancellationException())
          resetBarrier.await()
        } else reset()
      }

      // We're already resetting, await all waiters to finish
      is Resetting -> original.unblock.await()
    }
  }

  /**
   * When [await] is called the function will suspend until the required number of coroutines have reached the barrier.
   * Once the [capacity] of the barrier has been reached, the coroutine will be released and continue execution.
   */
  public suspend fun await() {
    state.loop { state ->
      when (state) {
        is Awaiting -> {
          val (awaiting, epoch, unblock) = state
          val awaitingNow = awaiting - 1
          if (awaitingNow == 0 && this.state.compareAndSet(state, Awaiting(capacity, epoch + 1, CompletableDeferred()))) {
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
                  s is Awaiting && s.epoch == epoch -> s.copy(awaiting = s.awaiting + 1)
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
                state, Awaiting(capacity, original.epoch + 1, CompletableDeferred())
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
