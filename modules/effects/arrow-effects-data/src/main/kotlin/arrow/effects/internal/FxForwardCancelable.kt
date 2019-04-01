package arrow.effects.internal

import arrow.core.Either
import arrow.core.Left
import arrow.core.NonFatal
import arrow.core.Right
import arrow.effects.*
import arrow.effects.CancelToken
import arrow.effects.suspended.fx.*
import java.util.concurrent.atomic.AtomicReference

/**
 * A placeholder for a [CancelToken] that will be set at a later time, the equivalent of a
 * `Promise<ForIO, CancelToken<ForIO>>`.
 * Used in the implementation of `bracket`, see [IOBracket].
 */
class FxForwardCancelable {

  private val state = AtomicReference<State>(init)

  fun cancel(): CancelToken<ForFx> {
    suspend fun loop(conn: KindConnection<ForFx>, cb: (Either<Throwable, Unit>) -> Unit): Unit = state.get().let { current ->
      when (current) {
        is State.Empty -> if (!state.compareAndSet(current, State.Empty(listOf(cb) + current.stack)))
          loop(conn, cb)

        is State.Active -> {
          state.lazySet(finished) // GC purposes
          // TODO this runs in an immediate execution context in cats-effect
          FxRunLoop.start(current.token, conn)
        }
      }
    }

    return Fx.asyncF { conn, cb ->
      Fx { loop(conn, cb) }
    }
  }

  suspend fun complete(value: CancelToken<ForFx>): Unit = state.get().let { current ->
    when (current) {
      is State.Active -> {
        FxRunLoop.start(value, KindConnection.uncancelable(FxAP))
        throw IllegalStateException(current.toString()) // ???
      }
      is State.Empty -> if (current == init) {
        // If `init`, then `cancel` was not triggered yet
        if (!state.compareAndSet(current, State.Active(value)))
          complete(value)
      } else {
        if (!state.compareAndSet(current, finished)) complete(value)
        else execute(value, current.stack)
      }
    }
  }

  companion object {

    /**
     * Models the internal state of [FxForwardCancelable]:
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
      data class Active(val token: CancelToken<ForFx>) : State()
    }

    private val init: State = State.Empty(listOf())
    private val finished: State = State.Active(Fx.unit())

    private suspend fun execute(token: CancelToken<ForFx>, stack: List<(Either<Throwable, Unit>) -> Unit>): Unit {
      // TODO this runs in an immediate execution context in cats-effect

      val r = try {
        Right(FxRunLoop.start(token))
      } catch (e: Throwable) {
        Left(e)
      }

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
