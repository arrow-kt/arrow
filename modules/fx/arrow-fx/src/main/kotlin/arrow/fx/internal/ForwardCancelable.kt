package arrow.fx.internal

import arrow.core.Either
import arrow.core.NonFatal
import arrow.fx.CancelToken
import arrow.fx.IO
import arrow.fx.IOConnection
import arrow.fx.IOPartialOf
import arrow.fx.IORunLoop
import arrow.fx.fix
import arrow.fx.internal.ForwardCancelable.Companion.State.Active
import arrow.fx.internal.ForwardCancelable.Companion.State.Empty
import java.util.concurrent.atomic.AtomicReference

/**
 * A placeholder for a [CancelToken] that will be set at a later time, the equivalent of a
 * `Promise<ForIO, CancelToken<ForIO>>`. Used in the implementation of `bracket`, see [IOBracket].
 */
class ForwardCancelable<E> {

  private val state = AtomicReference<State<E>>(init())

  fun cancel(): CancelToken<IOPartialOf<E>> {
    fun loop(conn: IOConnection<E>, cb: (Either<E, Unit>) -> Unit): Unit = state.get().let { current ->
      when (current) {
        is Empty -> if (!state.compareAndSet(current, Empty(listOf(cb) + current.stack)))
          loop(conn, cb)

        is Active -> {
          state.lazySet(finished()) // GC purposes
          // TODO this runs in an immediate execution context in cats-effect
          IORunLoop.startCancelable(current.token, conn, cb)
        }
      }
    }

    return IO.Async { conn, cb -> loop(conn, cb) }
  }

  fun complete(value: CancelToken<IOPartialOf<E>>): Unit = state.get().let { current ->
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
        if (!state.compareAndSet(current, finished()))
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
    private sealed class State<E> {
      data class Empty<E>(val stack: List<(Either<E, Unit>) -> Unit>) : State<E>()
      data class Active<E>(val token: CancelToken<IOPartialOf<E>>) : State<E>()
    }

    private val init: State<Any?> = Empty(listOf())
    private val finished: State<Any?> = Active(IO.unit)
    private fun <E> init(): State<E> = init as State<E>
    private fun <E> finished(): State<E> = finished as State<E>

    private fun <E> execute(token: CancelToken<IOPartialOf<E>>, stack: List<(Either<E, Unit>) -> Unit>): Unit =
    // TODO this runs in an immediate execution context in cats-effect
      token.fix().unsafeRunAsync { r ->
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
