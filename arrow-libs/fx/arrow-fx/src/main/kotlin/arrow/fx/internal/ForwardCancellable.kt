package arrow.fx.internal

import arrow.core.NonFatal

import arrow.fx.IO
import arrow.fx.IOConnection
import arrow.fx.IOOf
import arrow.fx.IOResult
import arrow.fx.IORunLoop
import arrow.fx.fix
import arrow.fx.internal.ForwardCancellable.Companion.State.Active
import arrow.fx.internal.ForwardCancellable.Companion.State.Empty
import arrow.fx.typeclasses.CancelToken
import kotlinx.atomicfu.atomic

/**
 * A placeholder for a [CancelToken] that will be set at a later time, the equivalent of a
 * `Promise<ForIO, CancelToken<ForIO>>`. Used in the implementation of `bracket`, see [IOBracket].
 */
internal class ForwardCancellable {

  private val state = atomic(init)

  fun cancel(): IO<Nothing, Unit> {
    fun loop(conn: IOConnection, cb: (IOResult<Nothing, Unit>) -> Unit): Unit = state.value.let { current ->
      when (current) {
        is Empty -> if (!state.compareAndSet(current, Empty(listOf(cb) + current.stack)))
          loop(conn, cb)

        is Active -> {
          state.lazySet(finished) // GC purposes
          Platform.trampoline { IORunLoop.startCancellable(current.token, conn, cb) }
        }
      }
    }

    return IO.Async { conn, cb -> loop(conn, cb) }
  }

  fun <E> complete(value: IOOf<E, Unit>): Unit = state.value.let { current ->
    when (current) {
      is Active -> {
        value.fix().unsafeRunAsyncEither {}
        throw IllegalStateException(current.toString())
      }
      is Empty -> if (current == init) {
        // If `init`, then `cancel` was not triggered yet
        if (!state.compareAndSet(current, Active(value.rethrow)))
          complete(value)
      } else {
        if (!state.compareAndSet(current, finished))
          complete(value)
        else
          execute(value.rethrow, current.stack)
      }
    }
  }

  companion object {

    /**
     * Models the internal state of [ForwardCancellable]:
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
      data class Empty(val stack: List<(IOResult<Nothing, Unit>) -> Unit>) : State()
      data class Active(val token: IOOf<Nothing, Unit>) : State()
    }

    private val init: State = Empty(listOf())
    private val finished: State = Active(IO.unit)

    private fun execute(token: IOOf<Nothing, Unit>, stack: List<(IOResult<Nothing, Unit>) -> Unit>): Unit =
      Platform.trampoline {
        token.fix().unsafeRunAsyncEither { r ->
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
}
