package arrow.fx.coroutines

import kotlinx.atomicfu.atomic
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * ForwardCancellable can be used to forward cancellation when you want to inject an uncancelable piece of logic.
 * I.e. is used in `bracket` and `guaranteeCase` to schedule the `cancel` frame.
 */
internal class ForwardCancellable {

  private val state = atomic(init)

  fun cancel(): CancelToken {
    fun loop(conn: SuspendConnection, cb: (Result<Unit>) -> Unit): Unit = state.value.let { current ->
      when (current) {
        is State.Empty ->
          if (!state.compareAndSet(current, State.Empty(listOf(cb) + current.stack))) loop(conn, cb)
          else Unit

        is State.Active -> {
          state.lazySet(finished) // GC purposes
          // Platform.trampoline { }
          current.token.cancel.startCoroutineCancellable(CancellableContinuation(EmptyCoroutineContext, conn, cb))
        }
      }
    }

    return CancelToken {
      suspendCoroutine { cont ->
        loop(cont.context.connection(), cont::resumeWith)
      }
    }
  }

  fun complete(value: Disposable): Unit =
    complete(CancelToken { value.invoke() })

  fun complete(value: CancelToken): Unit = state.value.let { current ->
    when (current) {
      is State.Active -> {
        Platform.unsafeRunSync(value.cancel)
        throw IllegalStateException(current.toString())
      }
      is State.Empty -> if (current == init) {
        // If `init`, then `cancel` was not triggered yet
        if (!state.compareAndSet(current, State.Active(value)))
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
      data class Empty(val stack: List<(Result<Unit>) -> Unit>) : State()
      data class Active(val token: CancelToken) : State()
    }

    private val init: State = State.Empty(listOf())
    private val finished: State = State.Active(CancelToken.unit)

    private fun execute(token: CancelToken, stack: List<(Result<Unit>) -> Unit>): Unit =
//      Platform.trampoline {
      token.cancel.startCoroutine(Continuation(EmptyCoroutineContext) { r ->
        val errors = stack.fold(emptyList<Throwable>()) { acc, cb ->
          try {
            cb(r)
            acc
          } catch (t: Throwable) {
            acc + t.nonFatalOrThrow()
          }
        }
        // AsyncErrorHandler for exceptions from running CancelToken.
        if (errors.isNotEmpty()) throw Platform.composeErrors(errors.first(), errors.drop(1))
        else Unit
      })
  }
}
