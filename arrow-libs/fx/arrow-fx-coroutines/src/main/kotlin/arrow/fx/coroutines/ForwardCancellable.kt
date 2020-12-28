package arrow.fx.coroutines

import arrow.fx.coroutines.ForwardCancellable.Companion.State.Active
import arrow.fx.coroutines.ForwardCancellable.Companion.State.Empty
import kotlinx.atomicfu.atomic
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * ForwardCancellable is useful to forward cancellation when you want to inject an uncancellable piece of logic,
 * or defer cancellation and wire two different [SuspendConnection]'s together.
 *
 * ForwardCancellable can be cancelled and completed with a [CancelToken].
 * The following scenarios can occur:
 *
 *  - When `cancel` is called before `complete`, then `cancel`'s token is back-pressured until `complete` is called.
 *    Then `cancel`'s token will subsequently call the CancelToken that was passed to `complete`.
 *
 *  - When `cancel` is called after `complete`, then `cancel` immediately runs the cancellation token passed to `complete`.
 *
 *  - Calling `complete` twice will result in an [ArrowInternalException], and is considered an internal developer bug.
 *
 * You can find example of this usage in [bracket] and [guaranteeCase] to schedule the `cancel` frame and
 * in [uncancellable] to back-pressure the original context until the uncancellable context is finished.
 *
 * @param ctx, the CoroutineContext where the `CancelToken` is run on.
 *   => This is taken into the constructor because we want to run it on the `CoroutineContext` of the creator of the `CancelToken`,
 *   and not on the `CoroutineContext` of the caller of `cancel`.
 *   => We want the `CancelToken` from `cancellable` to run on the `CoroutineContext` where `cancellable` is  called.
 *   => We want the release function of `bracket` & `bracketCase` to run on the `CoroutineContext` where `bracketX` is called.
 */
internal class ForwardCancellable(private val ctx: CoroutineContext) {

  private val state = atomic(init)

  suspend fun cancel(): Unit {
    fun loop(cb: (Result<Unit>) -> Unit): Unit = state.value.let { current ->
      when (current) {
        is State.Empty ->
          if (!state.compareAndSet(current, State.Empty(listOf(cb) + current.stack))) loop(cb)
          else Unit

        is State.Active -> {
          state.lazySet(finished) // GC purposes
          current.token.cancel.startCoroutine(Continuation(ctx + SuspendConnection.uncancellable, cb))
        }
      }
    }

    return suspendCoroutine { cont ->
      loop(cont::resumeWith)
    }
  }

  fun complete(value: Disposable): Unit =
    complete(CancelToken { value.invoke() })

  fun complete(value: CancelToken): Unit = state.value.let { current ->
    when (current) {
      is State.Active -> {
        // complete called twice, immediately run token in a blocking manner and throw ArrowInternalException
        Platform.unsafeRunSync(value.cancel)
        throw ArrowInternalException("$ArrowExceptionMessage\nForwardCancellable.complete called twice")
      }
      is State.Empty -> if (current == init) {
        // If `init`, then `cancel` was not triggered yet
        if (!state.compareAndSet(current, State.Active(value)))
          complete(value)
      } else {
        if (!state.compareAndSet(current, finished)) complete(value)
        else execute(ctx, value, current.stack)
      }
    }
  }

  companion object {

    /**
     * Models the internal state of [ForwardCancellable]:
     *
     *  - on start, the state is [Empty] of `emptyList`, aka [init]
     *  - on `cancel`, if no token was assigned yet, then the state will
     *    remain [Empty] with a non-empty `List<Callback>`
     *  - if a `CancelToken` is provided without `cancel` happening,
     *    then the state transitions to [Active] mode
     *  - on `cancel`, if the state was [Active], or if it was [Empty],
     *    regardless, the state transitions to `Active(CancelToken.unit)`, aka [finished]
     */
    private sealed class State {
      data class Empty(val stack: List<(Result<Unit>) -> Unit>) : State()
      data class Active(val token: CancelToken) : State()
    }

    private val init: State = State.Empty(listOf())
    private val finished: State = State.Active(CancelToken.unit)

    private fun execute(ctx: CoroutineContext, token: CancelToken, stack: List<(Result<Unit>) -> Unit>): Unit =
      token.cancel.startCoroutineUnintercepted(Continuation(ctx + SuspendConnection.uncancellable) { r ->
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
