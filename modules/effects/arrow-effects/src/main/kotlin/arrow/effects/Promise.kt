package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.internal.ImmediateContext
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.mapUnit
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
   * ```kotlin:ank:playground
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
  fun get(): Kind<F, A>

  /**
   * Try get the promised value, it returns [None] if promise is not fulfilled yet.
   * Returns [Some] of [A] if promise is fulfilled, [None] otherwise.
   *
   * ```kotlin:ank:playground
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
  fun tryGet(): Kind<F, Option<A>>

  /**
   * Completes, or fulfills, the promise with the specified value [A].
   * Results in an [Promise.AlreadyFulfilled] within [F] if the promise is already fulfilled.
   *
   * ```kotlin:ank:playground
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
   * ```kotlin:ank:playground
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
   * ```kotlin:ank:playground
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
   * ```kotlin:ank:playground
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
     *
     * ```kotlin:ank:playground
     * import arrow.effects.*
     * import arrow.effects.instances.io.concurrent.concurrent
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val promise: IOOf<Promise<ForIO, Int>> = Promise(IO.concurrent())
     *   //sampleEnd
     * }
     * ```
     */
    operator fun <F, A> invoke(CF: Concurrent<F>): Kind<F, Promise<F, A>> =
      CF.delay { unsafeCancelable<F, A>(CF) }

    /**
     * Creates an empty `Promise` from on [Concurrent] instance for [F].
     * This method is considered unsafe because it is not referentially transparent -- it allocates mutable state.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.*
     * import arrow.effects.instances.io.concurrent.concurrent
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val unsafePromise: Promise<ForIO, Int> = Promise.unsafeCancelable(IO.concurrent())
     *   //sampleEnd
     * }
     * ```
     */
    fun <F, A> unsafeCancelable(CF: Concurrent<F>): Promise<F, A> = CancelablePromise(CF)

    /**
     * Creates an empty `Promise` from on [Async] instance for [F].
     * Does not support cancellation of [get] operation.
     *
     * ```kotlin:ank:playground
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
    fun <F, A> uncancelable(AS: Async<F>): Kind<F, Promise<F, A>> =
      AS.delay { unsafeUncancelable<F, A>(AS) }

    /**
     * Creates an empty `Promise` from on [Async] instance for [F].
     * Does not support cancellation of [get] operation.
     * This method is considered unsafe because it is not referentially transparent -- it allocates mutable state.
     *
     * ```kotlin:ank:playground
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

internal class CancelablePromise<F, A>(CF: Concurrent<F>) : Promise<F, A>, Concurrent<F> by CF {

  internal class CallbackId
  internal sealed class State<out A> {
    data class Pending<A>(val joiners: Map<CallbackId, (Either<Throwable, A>) -> Unit>) : State<A>()
    data class Full<A>(val value: A) : State<A>()
    data class Error<A>(val throwable: Throwable) : State<A>()
  }

  private val state: AtomicReference<State<A>> = AtomicReference(State.Pending(emptyMap()))

  override fun get(): Kind<F, A> = defer {
    when (val current = state.get()) {
      is State.Full -> just(current.value)
      is State.Pending -> cancelable { cb ->
        val id = unsafeRegister(cb)
        tailrec fun unregister(): Unit = when (val current = state.get()) {
          is State.Full -> Unit
          is State.Error -> Unit
          is State.Pending -> {
            val updated = State.Pending(current.joiners - id)
            if (state.compareAndSet(current, updated)) Unit
            else unregister()
          }
        }
        delay { unregister() }
      }
      is State.Error -> raiseError(current.throwable)
    }
  }

  override fun tryGet(): Kind<F, Option<A>> = delay {
    when (val current = state.get()) {
      is State.Full -> Some(current.value)
      is State.Pending -> None
      is State.Error -> None
    }
  }

  override fun complete(a: A): Kind<F, Unit> =
    tryComplete(a).flatMap { didComplete ->
      if (didComplete) just(Unit)
      else raiseError(Promise.AlreadyFulfilled)
    }

  override fun tryComplete(a: A): Kind<F, Boolean> =
    defer { unsafeTryComplete(a) }

  override fun error(throwable: Throwable): Kind<F, Unit> =
    tryError(throwable).flatMap { didError ->
      if (didError) just(Unit)
      else raiseError(Promise.AlreadyFulfilled)
    }

  override fun tryError(throwable: Throwable): Kind<F, Boolean> =
    defer { unsafeTryError(throwable) }

  tailrec fun unsafeTryComplete(a: A): Kind<F, Boolean> = when (val current = state.get()) {
    is State.Full -> just(false)
    is State.Error -> just(false)
    is State.Pending -> {
      if (state.compareAndSet(current, State.Full(a))) {
        val list = current.joiners.values
        if (list.isNotEmpty()) notify(a, list).map { true }
        else just(true)
      } else unsafeTryComplete(a)
    }
  }

  tailrec fun unsafeTryError(error: Throwable): Kind<F, Boolean> = when (val current = state.get()) {
    is State.Full -> just(false)
    is State.Error -> just(false)
    is State.Pending -> {
      if (state.compareAndSet(current, State.Error(error))) {
        val list = current.joiners.values
        if (list.isNotEmpty()) notifyError(error, list).map { true }
        else just(true)
      } else unsafeTryError(error)
    }
  }

  private fun notify(a: A, list: Collection<(Either<Throwable, A>) -> Unit>): Kind<F, Unit> {
    val rightA = Right(a)

    return list.fold(unit()) { acc, next ->
      acc.flatMap { delay { next(rightA) }.startF(ImmediateContext).map(mapUnit) }
    }
  }

  private fun notifyError(error: Throwable, list: Collection<(Either<Throwable, A>) -> Unit>): Kind<F, Unit> {
    val leftError = Left(error)
    return list.fold(unit()) { acc, next ->
      acc.flatMap { delay { next(leftError) }.startF(ImmediateContext).map(mapUnit) }
    }
  }

  private fun unsafeRegister(cb: (Either<Throwable, A>) -> Unit): CallbackId {
    val id = CallbackId()

    tailrec fun register(): Either<Throwable, A>? = when (val current = state.get()) {
      is State.Full -> Right(current.value)
      is State.Pending -> {
        val updated = State.Pending(current.joiners + Pair(id, cb))
        if (state.compareAndSet(current, updated)) null
        else register()
      }
      is State.Error -> Left(current.throwable)
    }

    register()?.fold({ e -> cb(Left(e)) }, { a -> cb(Right(a)) })
    return id
  }

}

internal class UncancelablePromise<F, A>(AS: Async<F>) : Promise<F, A>, Async<F> by AS {
  internal sealed class State<out A> {
    data class Pending<A>(val joiners: List<(Either<Throwable, A>) -> Unit>) : State<A>()
    data class Full<A>(val value: A) : State<A>()
    data class Error<A>(val throwable: Throwable) : State<A>()
  }

  private val state: AtomicReference<State<A>> = AtomicReference(State.Pending(emptyList()))

  override fun get(): Kind<F, A> = async { k: (Either<Throwable, A>) -> Unit ->
    calculateNewGetState(k)
    loop(k)
  }

  private tailrec fun loop(k: (Either<Throwable, A>) -> Unit): Unit =
    when (val st = state.get()) {
      is State.Pending<A> -> {
        loop(k)
      }
      is State.Full -> k(Right(st.value))
      is State.Error -> k(Left(st.throwable))
    }

  private tailrec fun calculateNewGetState(k: (Either<Throwable, A>) -> Unit): Unit {
    val oldState = state.get()
    val newState = when (oldState) {
      is State.Pending<A> -> State.Pending(oldState.joiners + k)
      is State.Full -> oldState
      is State.Error -> oldState
    }
    return if (state.compareAndSet(oldState, newState)) Unit else calculateNewGetState(k)
  }

  override fun tryGet(): Kind<F, Option<A>> =
    when (val oldState = state.get()) {
      is State.Full -> just(Some(oldState.value))
      is State.Pending<A> -> just(None)
      is State.Error -> just(None)
    }


  override fun complete(a: A): Kind<F, Unit> =
    tryComplete(a).flatMap { didComplete ->
      if (didComplete) just(Unit) else raiseError(Promise.AlreadyFulfilled)
    }

  override fun tryComplete(a: A): Kind<F, Boolean> = defer {
    when (val oldState = state.get()) {
      is State.Pending -> {
        calculateNewTryCompleteState(a)
        just(true)
      }
      is State.Full -> just(false)
      is State.Error -> just(false)
    }
  }

  private tailrec fun calculateNewTryCompleteState(a: A): Unit {
    val oldState = state.get()
    val newState = when (oldState) {
      is State.Pending<A> -> State.Full(a)
      is State.Full -> oldState
      is State.Error -> oldState
    }

    if (state.compareAndSet(oldState, newState)) Unit else calculateNewTryCompleteState(a)
  }

  override fun error(throwable: Throwable): Kind<F, Unit> =
    tryError(throwable).flatMap { didError ->
      if (didError) just(Unit) else raiseError(Promise.AlreadyFulfilled)
    }

  override fun tryError(throwable: Throwable): Kind<F, Boolean> =
    defer { unsafeTryError(throwable) }

  tailrec fun unsafeTryError(error: Throwable): Kind<F, Boolean> = when (val current = state.get()) {
    is State.Full -> just(false)
    is State.Error -> just(false)
    is State.Pending ->
      if (state.compareAndSet(current, State.Error(error))) just(true)
      else unsafeTryError(error)
  }

}
