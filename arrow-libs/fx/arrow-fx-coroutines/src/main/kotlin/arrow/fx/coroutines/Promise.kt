package arrow.fx.coroutines

import arrow.core.Either
import arrow.fx.coroutines.DefaultPromise.State.Complete
import arrow.fx.coroutines.DefaultPromise.State.Pending
import arrow.fx.coroutines.Promise.AlreadyFulfilled
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

/**
 * When made, a [Promise] is empty. Until it is fulfilled, which can only happen once.
 *
 * A `Promise` is commonly used to provide and receive a value from 2 different threads,
 * since `Promise` can only be completed once unlike [ConcurrentVar] we can consider it a synchronization primitive.
 *
 * Let's say we wanted to await a `Fiber`, we could complete a Promise `latch` to signal it finished.
 * Awaiting the latch `Promise` will now prevent `main` from finishing early.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   val await = Promise<Unit>()
 *
 *   ForkConnected {
 *     println("Fiber starting up!")
 *     sleep(3.seconds)
 *     println("Fiber finished!")
 *     await.complete(Unit)
 *   }
 *
 *   await.get() // Suspend until fiber finishes
 * }
 * ```
 */
interface Promise<A> {

  /**
   * Get or throw the promised value, use `attempt` when throwing is not required.
   * Suspends until the promised value is available.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val promise = Promise<Int>()
   *
   *   timeOutOrNull(2.seconds) {
   *     promise.get()
   *   }.also { println("I timed out: $it") }
   *
   *   promise.complete(5)
   *   println(promise.get())
   *   //sampleEnd
   * }
   * ```
   */
  suspend fun get(): A

  /**
   * Try get the promised value, it returns `null` if promise is not fulfilled yet.
   * Returns [A] if promise is fulfilled.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val promise = Promise<Int>()
   *   val empty = promise.tryGet()
   *   promise.complete(1)
   *   val full = promise.tryGet()
   *   //sampleEnd
   *   println("empty: $empty, full: $full")
   * }
   * ```
   */
  suspend fun tryGet(): A?

  /**
   * Completes, or fulfills, the promise with the specified value [A].
   * Returns [Promise.AlreadyFulfilled] in [Either.Left] if the promise is already fulfilled.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val p = Promise<Int>()
   *   p.complete(1)
   *   val r1 = p.get()
   *   val failed = p.complete(2)
   *   val r2 = p.get()
   *   //sampleEnd
   *   println("r1: $r1, failed: $failed, r2: $r2")
   * }
   * ```
   */
  suspend fun complete(a: A): Either<AlreadyFulfilled, Unit>

  companion object {
    fun <A> unsafe(): Promise<A> =
      DefaultPromise()

    suspend operator fun <A> invoke(): Promise<A> =
      unsafe()
  }

  object AlreadyFulfilled {
    internal val left: Either<AlreadyFulfilled, Nothing> = Either.Left(this)
  }
}

internal class DefaultPromise<A> : Promise<A> {

  internal sealed class State<out A> {
    data class Pending<A>(val joiners: Map<Token, (Result<A>) -> Unit>) : State<A>()
    data class Complete<A>(val value: A) : State<A>()
  }

  private val state: AtomicRef<State<A>> = atomic(Pending(emptyMap()))

  override suspend fun get(): A =
    when (val current = state.value) {
      is Complete -> current.value
      is Pending -> cancellable { cb ->
        val id = unsafeRegister(cb)
        CancelToken { unregister(id) }
      }
    }

  override suspend fun tryGet(): A? =
    when (val current = state.value) {
      is Complete -> current.value
      is Pending -> null
      is Error -> null
    }

  override suspend fun complete(a: A): Either<AlreadyFulfilled, Unit> =
    unsafeTryComplete(a)

  private tailrec suspend fun unsafeTryComplete(a: A): Either<AlreadyFulfilled, Unit> =
    when (val current = state.value) {
      is Complete -> AlreadyFulfilled.left
      is Error -> AlreadyFulfilled.left
      is Pending -> {
        if (state.compareAndSet(current, Complete(a))) {
          val joiners = current.joiners.values
          if (joiners.isNotEmpty()) {
            joiners.callAll(Result.success(a))
          }
          Either.Right(Unit)
        } else unsafeTryComplete(a)
      }
    }

  private fun Iterable<(Result<A>) -> Unit>.callAll(value: Result<A>): Unit =
    forEach { cb -> cb(value) }

  @Suppress("RESULT_CLASS_WITH_NULLABLE_OPERATOR")
  private fun unsafeRegister(cb: (Result<A>) -> Unit): Token {
    val id = Token()
    register(id, cb)?.let(cb)
    return id
  }

  @Suppress("RESULT_CLASS_IN_RETURN_TYPE")
  private tailrec fun register(id: Token, cb: (Result<A>) -> Unit): Result<A>? =
    when (val current = state.value) {
      is Complete -> Result.success(current.value)
      is Pending -> {
        val updated = Pending(current.joiners + Pair(id, cb))
        if (state.compareAndSet(current, updated)) null
        else register(id, cb)
      }
    }

  private tailrec fun unregister(id: Token): Unit = when (val current = state.value) {
    is Complete -> Unit
    is Error -> Unit
    is Pending -> {
      val updated = Pending(current.joiners - id)
      if (state.compareAndSet(current, updated)) Unit
      else unregister(id)
    }
  }

  override fun toString(): String =
    "Promise@${hashCode()}"
}
