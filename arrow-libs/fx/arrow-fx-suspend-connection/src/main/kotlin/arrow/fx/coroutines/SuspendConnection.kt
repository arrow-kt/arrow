package arrow.fx.coroutines

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/**
 * [SuspendConnection] is a [CoroutineContext] and has two implementations:
 *   - [Uncancellable]
 *   - [DefaultConnection]
 *
 * # Uncancellable
 *
 * The [Uncancellable] implementation has a no-op [cancel] method, and always returns [false] upon checking [isCancelled].
 * Which subsequently means that all cancellable operations become uncancellable when run on this [CoroutineContext],
 * and inserting [cancelBoundary] becomes a no-op operation.
 * Some optimisations could be implemented when an [Uncancellable] context is detected,
 * such as [bracket] could run as a simple `try/catch/finally` since [ExitCase.Cancelled] can never occur on a [Uncancellable] connection.
 * See the [arrow.fx.coroutines.uncancellable] combinator implementation to see how you can swap from any kind of [SuspendConnection] to a [Uncancellable] and back to the original connection.
 *
 * # DefaultConnection
 *
 * [DefaultConnection] keeps track of all [CancelToken] registered for a given [CoroutineContext].
 * It does so by keeping all [CancelToken] in a FIFO stack, and running them in order when cancelled.
 *
 * Since [SuspendConnection] is a stack, we can register a token before running a cancellable operation,
 * and pop the token after the cancellable operation has finished so
 * we don't run a [CancelToken] when it's not necessary anymore since that could lead to (undefined) weird behavior.
 * You can see this usage in [parMapN], [raceN], [racePair] & [raceTriple],
 * where we create multiple new connections and push them onto the stack with [push] and remove them with [pop]
 * when the operations running on those connections terminate.
 */
sealed class SuspendConnection : AbstractCoroutineContextElement(SuspendConnection) {

  abstract suspend fun cancel(): Unit

  abstract fun isCancelled(): Boolean
  abstract fun push(tokens: List<suspend () -> Unit>): Unit
  fun isNotCancelled(): Boolean = !isCancelled()

  abstract fun push(token: suspend () -> Unit): Unit

  fun pushPair(lh: SuspendConnection, rh: SuspendConnection): Unit =
    pushPair({ lh.cancel() }, { rh.cancel() })

  fun pushPair(lh: suspend () -> Unit, rh: suspend () -> Unit): Unit =
    push(listOf(lh, rh))

  abstract fun pop(): suspend () -> Unit
  abstract fun tryReactivate(): Boolean

  companion object Key : CoroutineContext.Key<SuspendConnection> {
    val uncancellable: SuspendConnection = Uncancellable
    operator fun invoke(): SuspendConnection = DefaultConnection()
  }

  object Uncancellable : SuspendConnection() {
    override suspend fun cancel() = Unit
    override fun isCancelled(): Boolean = false
    override fun push(tokens: List<suspend () -> Unit>) = Unit
    override fun push(token: suspend () -> Unit) = Unit
    override fun pop(): suspend () -> Unit = suspend { Unit }
    override fun tryReactivate(): Boolean = true
    override fun toString(): String = "UncancellableConnection"
  }

  class DefaultConnection : SuspendConnection() {
    private val state: AtomicRef<List<suspend () -> Unit>?> = atomic(emptyList())

    override suspend fun cancel(): Unit =
      state.getAndSet(null).let { stack ->
        when {
          stack == null || stack.isEmpty() -> Unit
          else -> stack.cancelAll()
        }
      }

    override fun isCancelled(): Boolean = state.value == null

    override tailrec fun push(token: suspend () -> Unit): Unit = when (val list = state.value) {
      // If connection is already cancelled cancel token immediately.
      null -> token
        .startCoroutine(Continuation(EmptyCoroutineContext) { })
      else ->
        if (state.compareAndSet(list, listOf(token) + list)) Unit
        else push(token)
    }

    override fun push(tokens: List<suspend () -> Unit>) =
      push { tokens.cancelAll() }

    override tailrec fun pop(): suspend () -> Unit {
      val state = state.value
      return when {
        state == null || state.isEmpty() -> suspend { Unit }
        else ->
          if (this.state.compareAndSet(state, state.drop(1))) state.first()
          else pop()
      }
    }

    override fun tryReactivate(): Boolean =
      state.compareAndSet(null, emptyList())

    private suspend fun List<suspend () -> Unit>.cancelAll(): Unit =
      forEach { it.invoke() }

    override fun toString(): String =
      "SuspendConnection(isCancelled = ${isCancelled()}, size= ${state.value?.size ?: 0})"
  }
}
