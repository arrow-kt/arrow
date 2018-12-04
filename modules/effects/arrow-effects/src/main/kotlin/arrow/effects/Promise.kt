package arrow.effects

import arrow.Kind
import arrow.core.*
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
   * Suspending the Fiber running the action until the result is available.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.typeclasses.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val promise = Promise.uncancelable<ForIO, Int>(IO.async())
   *
   *   promise.flatMap { p ->
   *     p.get
   *   }  //Never ends since is uncancelable
   *
   *   promise.flatMap { p ->
   *     p.complete(1).flatMap {
   *       p.get
   *     }
   *   }.unsafeRunSync() == IO.just(1).unsafeRunSync()
   *   //sampleEnd
   * }
   * ```
   */
  val get: Kind<F, A>

  /**
   * Try get the promised value, it returns [None] if promise is not fulfilled yet.
   * Returns [Some] of [A] if promise is fulfilled, [None] otherwise.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.*
   * import arrow.effects.*
   * import arrow.effects.typeclasses.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val promise = Promise.uncancelable<ForIO, Int>(IO.async())
   *
   *   promise.flatMap { p ->
   *     p.tryGet
   *   }.unsafeRunSync() == None
   *
   *   promise.flatMap { p ->
   *     p.complete(1).flatMap {
   *       p.tryGet
   *     }
   *   }.unsafeRunSync() == Some(1)
   *   //sampleEnd
   * }
   * ```
   */
  val tryGet: Kind<F, Option<A>>

  /**
   * Completes, or fulfills, the promise with the specified value [A].
   * Results in an [Promise.AlreadyFulfilled] within [F] if the promise is already fulfilled.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val promise = Promise.uncancelable<ForIO, Int>(IO.async())
   *
   *   promise.flatMap { p ->
   *     p.complete(1).flatMap {
   *       p.get
   *     }
   *   }.unsafeRunSync() == IO.just(1).unsafeRunSync()
   *
   *   promise.flatMap { p ->
   *     p.complete(1).flatMap {
   *       p.complete(2)
   *     }
   *   }.attempt().unsafeRunSync() ==
   *     IO.raiseError<Int>(Promise.AlreadyFulfilled).attempt().unsafeRunSync()
   *   //sampleEnd
   * }
   * ```
   */
  fun complete(a: A): Kind<F, Unit>

  /**
   * Try to complete, or fulfill, the promise with the specified value [A].
   * Returns `true` if the promise successfully completed, `false` otherwise.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val promise = Promise.uncancelable<ForIO, Int>(IO.async())
   *
   *   promise.flatMap { p ->
   *     p.tryComplete(1)
   *   }.unsafeRunSync() == true
   *
   *   promise.flatMap { p ->
   *     p.complete(1).flatMap {
   *       p.tryComplete(2)
   *     }
   *   }.unsafeRunSync() == false
   *   //sampleEnd
   * }
   * ```
   */
  fun tryComplete(a: A): Kind<F, Boolean>

  /**
   * Errors the promise with the specified [Throwable].
   * Results in an [Promise.AlreadyFulfilled] within [F] if the promise is already fulfilled.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val promise = Promise.uncancelable<ForIO, Int>(IO.async())
   *
   *   promise.flatMap { p ->
   *     p.error(RuntimeException("Boom"))
   *   }.attempt().unsafeRunSync() ==
   *     IO.raiseError<Int>(RuntimeException("Boom")).attempt().unsafeRunSync()
   *
   *   promise.flatMap { p ->
   *     p.complete(1).flatMap {
   *       p.error(RuntimeException("Boom"))
   *     }
   *   }.attempt().unsafeRunSync() ==
   *     IO.raiseError<Int>(Promise.AlreadyFulfilled).attempt().unsafeRunSync()
   *   //sampleEnd
   * }
   * ```
   */
  fun error(throwable: Throwable): Kind<F, Unit>

  /**
   * Tries to error the promise with the specified [Throwable].
   * Returns `true` if the promise already completed or errored, `false` otherwise.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.core.Right
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val promise = Promise.uncancelable<ForIO, Int>(IO.async())
   *   val throwable = RuntimeException("Boom")
   *
   *   promise.flatMap { p ->
   *     p.tryError(throwable)
   *   }.attempt().unsafeRunSync() ==
   *     IO.raiseError<Int>(throwable).attempt().unsafeRunSync()
   *
   *   promise.flatMap { p ->
   *     p.complete(1).flatMap {
   *       p.tryError(RuntimeException("Boom"))
   *     }
   *   }.attempt().unsafeRunSync() == Right(false)
   *   //sampleEnd
   * }
   * ```
   */
  fun tryError(throwable: Throwable): Kind<F, Boolean>

  companion object {

    /**
     * Creates an empty `Promise` from on [Async] instance for [F].
     * Does not support cancellation of [get] operation.
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.effects.*
     * import arrow.effects.instances.io.async.async
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val promise: IOOf<Promise<ForIO, Int>> = Promise.uncancelable(IO.async())
     *   //sampleEnd
     * }
     * ```
     */
    fun <F, A> uncancelable(AS: Async<F>): Kind<F, Promise<F, A>> = AS.delay { unsafeUncancelable<F, A>(AS) }

    /**
     * Creates an empty `Promise` from on [Async] instance for [F].
     * Does not support cancellation of [get] operation.
     * This method is considered unsafe because it is not referentially transparent -- it allocates mutable state.
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.effects.*
     * import arrow.effects.instances.io.async.async
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val unsafePromise: Promise<ForIO, Int> = Promise.unsafeUncancelable(IO.async())
     *   //sampleEnd
     * }
     * ```
     */
    fun <F, A> unsafeUncancelable(AS: Async<F>): Promise<F, A> = UncancelablePromise(AS)

  }

  object AlreadyFulfilled : Throwable(message = "Promise was already fulfilled")

}

internal class UncancelablePromise<F, A>(AS: Async<F>) : Promise<F, A>, Async<F> by AS {

  private val state: AtomicReference<PromiseState<A>> = AtomicReference(PromiseState.Pending(emptyList()))

  override val get: Kind<F, A> = async { k: (Either<Throwable, A>) -> Unit ->
    calculateNewGetState(k)
    loop(k)
  }

  private tailrec fun loop(k: (Either<Throwable, A>) -> Unit): Unit {
    val st = state.get()
    when (st) {
      is PromiseState.Pending<A> -> loop(k)
      is PromiseState.Full -> k(Right(st.value))
      is PromiseState.Error -> k(Left(st.throwable))
    }
  }

  private tailrec fun calculateNewGetState(k: (Either<Throwable, A>) -> Unit): Unit {
    val oldState = state.get()
    val newState = when (oldState) {
      is PromiseState.Pending<A> -> PromiseState.Pending(oldState.joiners + k)
      is PromiseState.Full -> oldState
      is PromiseState.Error -> oldState
    }
    return if (state.compareAndSet(oldState, newState)) Unit else calculateNewGetState(k)
  }

  override val tryGet: Kind<F, Option<A>>
    get() {
      val oldState = state.get()
      return when (oldState) {
        is PromiseState.Full -> just(Some(oldState.value))
        is PromiseState.Pending<A> -> just(None)
        is PromiseState.Error -> just(None)
      }
    }

  override fun complete(a: A): Kind<F, Unit> =
    tryComplete(a).flatMap { didComplete ->
      if (didComplete) just(Unit) else raiseError(Promise.AlreadyFulfilled)
    }

  override fun tryComplete(a: A): Kind<F, Boolean> = defer {
    val oldState = state.get()
    when (oldState) {
      is PromiseState.Pending -> {
        calculateNewTryCompleteState(a)
        just(true)
      }
      is PromiseState.Full -> just(false)
      is PromiseState.Error -> just(false)
    }
  }

  private tailrec fun calculateNewTryCompleteState(a: A): Unit {
    val oldState = state.get()
    val newState = when (oldState) {
      is PromiseState.Pending<A> -> PromiseState.Full(a)
      is PromiseState.Full -> oldState
      is PromiseState.Error -> oldState
    }

    if (state.compareAndSet(oldState, newState)) Unit else calculateNewTryCompleteState(a)
  }

  override fun error(throwable: Throwable): Kind<F, Unit> =
    tryError(throwable).flatMap {
      raiseError<Unit>(Promise.AlreadyFulfilled)
    }

  override fun tryError(throwable: Throwable): Kind<F, Boolean> = state.get().let { oldState ->
    when (oldState) {
      is PromiseState.Pending -> raiseError(throwable)
      is PromiseState.Full -> just(false)
      is PromiseState.Error -> just(false)
    }
  }

}

internal sealed class PromiseState<out A> {
  data class Pending<A>(val joiners: List<(Either<Throwable, A>) -> Unit>) : PromiseState<A>()
  data class Full<A>(val value: A) : PromiseState<A>()
  data class Error<A>(val throwable: Throwable) : PromiseState<A>()
}