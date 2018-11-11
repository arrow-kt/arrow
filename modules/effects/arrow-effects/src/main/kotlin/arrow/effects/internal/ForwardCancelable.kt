package arrow.effects.internal

import arrow.core.Either
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.IORunLoop
import arrow.effects.fix
import arrow.effects.internal.ForwardCancelable.Companion.State.Active
import arrow.effects.internal.ForwardCancelable.Companion.State.Empty
import java.util.concurrent.atomic.AtomicReference

/**
 * A placeholder for a [CancelToken] that will be set at a later time, the equivalent of a
 * `Deferred[IO, CancelToken]`. Used in the implementation of `bracket`, see [IOBracket].
 */
class ForwardCancelable {

  private val state = AtomicReference<State>(init)

  fun cancel(): CancelToken<ForIO> {
    fun loop(conn: IOConnection, cb: (Either<Throwable, Unit>) -> Unit): Unit = state.get().let { current ->
      when (current) {
        is State.Empty -> if (!state.compareAndSet(current, State.Empty(listOf(cb) + current.stack)))
          loop(conn, cb)

        is Active -> {
          state.lazySet(finished) // GC purposes
          // TODO this runs in an immediate execution context in cats-effect
          IORunLoop.startCancelable(current.token.fix(), conn, cb)
        }
      }
    }

    return IO.Async { conn, cb -> loop(conn, cb) }
  }

  fun complete(value: CancelToken<ForIO>): Unit = state.get().let { current ->
    when (current) {
      is Active -> {
        value.fix().unsafeRunAsync {}
        throw IllegalStateException(current.toString())
      }
      is Empty -> if (current == init) {
        // If `init`, then `cancel` was not triggered yet
        if (!state.compareAndSet(current, Active(value)))
          complete(value)
      } else {
        if (!state.compareAndSet(current, finished))
          complete(value)
        else
          execute(value, current.stack)
      }
    }
  }

  companion object {

    /**
     * Models the internal state of [ForwardCancelable]:
     *
     *  - on start, the state is [Empty] of `Nil`, aka [init]
     *  - on `cancel`, if no token was assigned yet, then the state will
     *    remain [Empty] with a non-nil `List[Callback]`
     *  - if a `CancelToken` is provided without `cancel` happening,
     *    then the state transitions to [Active] mode
     *  - on `cancel`, if the state was [Active], or if it was [Empty],
     *    regardless, the state transitions to `Active(IO.unit)`, aka [finished]
     */
    private sealed class State {
      data class Empty(val stack: List<(Either<Throwable, Unit>) -> Unit>) : State()
      data class Active(val token: CancelToken<ForIO>) : State()
    }

    private val init: State = Empty(listOf())
    private val finished: State = Active(IO.unit)

    private fun execute(token: CancelToken<ForIO>, stack: List<(Either<Throwable, Unit>) -> Unit>): Unit =
    // TODO this runs in an immediate execution context in cats-effect
      token.fix().unsafeRunAsync { r ->
        stack.forEach { cb ->
          try {
            cb(r)
          } catch (nonFatal: Exception) {
            // Logger.reportFailure(e) // TODO should we ignore this?
          }
        }
      }
  }
}
