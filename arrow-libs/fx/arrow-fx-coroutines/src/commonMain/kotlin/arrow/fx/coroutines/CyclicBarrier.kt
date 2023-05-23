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

  private sealed class State {
    abstract val epoch: Long

    fun nextEpoch() = Awaiting(
      awaitingNow = 0,
      epoch = epoch + 1,
      unblock = CompletableDeferred()
    )
  }

  private data class Awaiting(
    /** Current number of waiting parties. **/
    val awaitingNow: Int,
    override val epoch: Long,
    val unblock: CompletableDeferred<Unit>
  ) : State() {

    fun incrementAwaiting(): State {
      check(awaitingNow >= 0)
      return Awaiting(
        awaitingNow = awaitingNow + 1,
        epoch = epoch,
        unblock = unblock
      )
    }
  }

  private data class Resetting(
    override val epoch: Long,
    /** Barrier used to ensure all awaiting threads are ready to reset. **/
    val resetBarrier: CyclicBarrier
  ) : State()

  private inner class StateMachine {
    val state: AtomicRef<State> = AtomicRef(Awaiting(0, 0, CompletableDeferred()))

    fun get() = state.get()

    fun tryIncrementAwaiting(expected: Awaiting) = state.compareAndSet(expected, expected.incrementAwaiting())

    fun tryStartNextEpoch(expected: Awaiting): Boolean {
      return state.compareAndSet(expected, expected.nextEpoch())
    }

    fun startNextEpoch() {
      val current = state.get()
      check(current is Awaiting)
      state.update { s -> s.nextEpoch() }
    }

    fun startReset() {
      state.loop { current ->
        if (current is Awaiting) {
          val resetState = Resetting(
            epoch = current.epoch,
            resetBarrier = CyclicBarrier(
              capacity = current.awaitingNow,
              barrierAction = { tryStartNextEpoch(current) })
          )
          if (state.compareAndSet(current, resetState)) {
            current.unblock.cancel()
            return
          }
        }
      }
    }
  }

  private val state = StateMachine()

  /** The number of parties currently waiting. **/
  public val numberWaiting: Int
    get() = state.state.get()
      .let {
        return when (it) {
          is Awaiting -> it.awaitingNow
          is Resetting -> 0
        }
      }

  /**
   * When called, all waiting coroutines will be cancelled with [CancellationException].
   * When all threads have been cancelled the barrier will cycle.
   */
  public fun reset(): Unit = if (numberWaiting == 0) {
    state.startNextEpoch()
  } else {
    state.startReset()
  }

  private fun attemptBarrierAction(unblock: CompletableDeferred<Unit>) {
    try {
      barrierAction.invoke()
    } catch (e: Exception) {
      val cancellationException =
        CancellationException("CyclicBarrier barrierAction failed with exception.", e)
      unblock.cancel(cancellationException)
      throw cancellationException
    }
  }

  /**
   * When [await] is called the function will suspend until the required number of coroutines have called [await].
   * Once the [capacity] of the barrier has been reached, the coroutine will be released and continue execution.
   */
  public suspend fun await() {
    while (true) {

      when (val current = state.state.get()) {
        is Resetting -> {
          current.resetBarrier.await()
          throw CancellationException("CyclicBarrier was reset.")
        }
        is Awaiting -> {
          val (remaining, epoch, unblock) = current
          val remainingNow = remaining - 1

          if (remainingNow == 0) {
            if (state.tryStartNextEpoch(current)) {
              attemptBarrierAction(unblock = unblock)
              unblock.complete(Unit)
              return
            } else continue
          } else if (state.tryIncrementAwaiting(current)) {
            return try {
              unblock.await()
            } catch (cancelled: CancellationException) {
              val cancelledState = state.get()
              when (cancelledState) {
                is Resetting -> {
                  cancelledState.resetBarrier.await()
                  throw CancellationException("CyclicBarrier was reset.")
                }
                is Awaiting -> {
                  state.state.update { s ->
                    if (s.epoch == cancelledState.epoch) cancelledState.copy(awaitingNow = cancelledState.awaitingNow + 1) else s
                  }
                  throw cancelled
                }
              }
            }
          }
        }
      }
    }
  }
}
