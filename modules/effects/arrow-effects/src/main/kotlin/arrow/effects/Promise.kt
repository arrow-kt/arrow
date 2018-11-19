package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.effects.typeclasses.Async
import java.util.concurrent.atomic.AtomicReference

/**
 * When made, a [Promise] is empty. Until it is fulfilled, which can only happen once.
 *
 * A [Promise] guarantees (promises) [A] at some point in the future within the context of [F].
 * Note that since [F] is constrained to [Async] an error can also occur.
 */
interface Promise<F, A> {

  /**
   * Get the promised value.
   * Suspending the fiber running the action until the result is available.
   *
   * ```kotlin
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * val promise = Promise.uncancelable<ForIO, Int>(IO.async())
   *
   * promise.flatMap { p ->
   *   p.get
   * } == IO.never()
   *
   * promise.flatMap { p ->
   *   p.complete(1).flatMap {
   *     p.get
   *   }
   * } == IO.just(1)
   * ```
   */
  val get: Kind<F, A>

  /**
   * Completes the promise with the specified value [A].
   *
   * Results in an [Promise.AlreadyFulfilled] within [F] if the promise is already fulfilled.
   *
   * ```kotlin
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * val promise = Promise.uncancelable<ForIO, Int>(IO.async())
   *
   * promise.flatMap { p ->
   *   p.complete(1).flatMap {
   *     p.get
   *   }
   * } == IO.just(1)
   *
   * promise.flatMap { p ->
   *   p.complete(1).flatMap {
   *     p.complete(2)
   *   }
   * } == IO.raiseError(Promise.AlreadyFulfilled)
   * ```
   */
  fun complete(a: A): Kind<F, Unit>

  /**
   * Errors the promise with the specified [Throwable].
   *
   * Results in an [Promise.AlreadyFulfilled] within [F] if the promise is already fulfilled.
   *
   * ```kotlin
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * val promise = Promise.uncancelable<ForIO, Int>(IO.async())
   *
   * promise.flatMap { p ->
   *   p.error(RuntimeException("Boom"))
   * } == IO.raiseError(RuntimeException("Boom"))
   *
   * promise.flatMap { p ->
   *   p.complete(1).flatMap {
   *     p.error(RuntimeException("Boom"))
   *   }
   * } == IO.raiseError(Promise.AlreadyFulfilled)
   * ```
   */
  fun error(throwable: Throwable): Kind<F, Unit>

  companion object {

    /**
     * Creates an empty `Promise` from on [Async] instance for [F].
     * Does not support cancellation of [get] operation.
     *
     * ```kotlin
     * import arrow.effects.*
     * import arrow.effects.instances.io.async.async
     *
     * val promise: IOOf<Promise<ForIO, Int>> = Promise.cancellable(IO.async())
     * ```
     */
    fun <F, A> uncancelable(AS: Async<F>): Kind<F, Promise<F, A>> = AS { unsafeCancellable<F, A>(AS) }

    /**
     * Creates an empty `Promise` from on [Async] instance for [F].
     * Does not support cancellation of [get] operation.
     * This method is considered unsafe because it is not referentially transparent -- it allocates mutable state.
     *
     * ```kotlin
     * import arrow.effects.*
     * import arrow.effects.instances.io.async.async
     *
     * val unsafePromise: Promise<ForIO, Int> = Promise.unsafeCancellable(IO.async())
     * ```
     */
    fun <F, A> unsafeCancellable(AS: Async<F>): Promise<F, A> = CancellablePromise(AS, AtomicReference(CancellablePromise.State.Pending(emptyList())))

  }

  object AlreadyFulfilled: Throwable(message = "Promise was already fulfilled")

}

internal class CancellablePromise<F, A> constructor(private val AS: Async<F>,
                                                    private val state: AtomicReference<State<A>>) : Promise<F, A> {

  override val get: Kind<F, A> = AS.async { k: (Either<Throwable, A>) -> Unit ->
    tailrec fun loop(): Unit {
      val st = state.get()
      when (st) {
        is State.Pending<A> -> loop()
        is State.Full -> k(Right(st.value))
        is State.Error -> k(Left(st.throwable))
      }
    }

    tailrec fun calculateNewState(): Unit {
      val oldState = state.get()
      val newState = when (oldState) {
        is State.Pending<A> -> State.Pending(oldState.joiners + k)
        is State.Full -> oldState
        is State.Error -> oldState
      }
      return if (state.compareAndSet(oldState, newState)) Unit else calculateNewState()
    }

    calculateNewState()
    loop()
  }

  override fun complete(a: A): Kind<F, Unit> = AS.defer {
    tailrec fun calculateNewState(): Unit {
      val oldState = state.get()
      val newState = when (oldState) {
        is State.Pending<A> -> State.Full(a)
        is State.Full -> oldState
        is State.Error -> oldState
      }
      return if (state.compareAndSet(oldState, newState)) Unit else calculateNewState()
    }
    val oldState = state.get()
    when (oldState) {
      is State.Pending -> calculateNewState().let { AS.just(Unit) }
      is State.Full -> AS.raiseError(Promise.AlreadyFulfilled)
      is State.Error -> AS.raiseError(Promise.AlreadyFulfilled)
    }
  }

  override fun error(throwable: Throwable): Kind<F, Unit> = state.get().let { oldState ->
    when (oldState) {
      is State.Pending -> AS.raiseError(throwable)
      is State.Full -> AS.raiseError(Promise.AlreadyFulfilled)
      is State.Error -> AS.raiseError(Promise.AlreadyFulfilled)
    }
  }

  internal sealed class State<out A> {
    data class Pending<A>(val joiners: List<(Either<Throwable, A>) -> Unit>) : State<A>()
    data class Full<A>(val value: A) : State<A>()
    data class Error<A>(val throwable: Throwable) : State<A>()
  }

}
