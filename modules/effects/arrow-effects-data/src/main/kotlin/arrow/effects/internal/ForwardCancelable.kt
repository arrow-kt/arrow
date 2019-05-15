package arrow.effects.internal

import arrow.core.Either
import arrow.core.NonFatal
import arrow.effects.CancelToken
import arrow.effects.ForIO
import arrow.effects.IOConnection
import arrow.effects.IO
import arrow.effects.typeclasses.mapUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * A placeholder for a [CancelToken] that will be set at a later time, the equivalent of a `Promise<ForIO, CancelToken<ForIO>>`.
 *
 * [ForwardCancelable] can take care of forwarding cancellation when you temporally switch contexts.
 *
 * i.e. when cancellation occurs during uncancellable resource acquisition, we need to forward the cancellation event
 * until after the resource acquisition is completed so we can cancel and call the release function.
 * @see [FxBracket.invoke]
 */
internal class ForwardCancelable {

  private val state = AtomicReference<State>(init)

  fun cancel(): CancelToken<ForIO> = IO.asyncF { conn, cb ->
    IO { loop(conn, cb) }
  }

  private fun loop(conn: IOConnection, cb: (Either<Throwable, Unit>) -> Unit): Unit = state.get().let { current ->
    when (current) {
      is State.Empty -> if (!state.compareAndSet(current, Companion.State.Empty(listOf(cb) + current.stack))) loop(conn, cb)
      is State.Active -> {
        state.lazySet(finished) // GC purposes
        // TODO this runs in an immediate execution context in cats-effect
        IORunLoop.startCancelable(current.token, token = conn, cb = cb)
      }
    }
  }

  fun complete(value: CancelToken<ForIO>): Unit = state.get().let { current ->
    when (current) {
      is State.Active -> {
        IORunLoop.start(value, cb = mapUnit)
        throw IllegalStateException(current.toString()) // ???
      }
      is State.Empty -> if (current == init) {
        // If `init`, then `cancel` was not triggered yet
        if (!state.compareAndSet(current, Companion.State.Active(value)))
          complete(value)
      } else {
        if (!state.compareAndSet(current, finished)) complete(value)
        else execute(value, current.stack)
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

    private val init: State = Companion.State.Empty(listOf())
    private val finished: State = Companion.State.Active(IO.unit)

    // TODO this runs in an immediate execution context in cats-effect
    private fun execute(token: CancelToken<ForIO>, stack: List<(Either<Throwable, Unit>) -> Unit>): Unit =
      IORunLoop.start(token) { r ->
        val errors = stack.fold(emptyList<Throwable>()) { acc, cb ->
          try {
            cb(r)
            acc
          } catch (t: Throwable) {
            if (NonFatal(t)) {
              acc + t
            } else {
              throw t
            }
          }
        }

        if (errors.isNotEmpty()) throw Platform.composeErrors(errors.first(), errors.drop(1))
        else Unit
      }
  }
}
