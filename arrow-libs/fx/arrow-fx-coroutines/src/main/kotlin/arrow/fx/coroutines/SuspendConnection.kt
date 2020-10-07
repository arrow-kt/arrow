package arrow.fx.coroutines

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

/**
 * Inline marker to mark a [CancelToken],
 * This allows for clearer APIs in functions that expect a [CancelToken] to be returned.
 */
@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class CancelToken(val cancel: suspend () -> Unit) {

  suspend fun invoke(): Unit = cancel.invoke()

  override fun toString(): String = "CancelToken(..)"

  companion object {
    val unit = CancelToken { Unit }
  }
}

typealias Disposable = () -> Unit

/**
 * Grab the [SuspendConnection] from [CoroutineContext],
 * when none is found then default to an uncancellable connection.
 *
 * That could only happen when we call [connection] inside a [suspend] function **not** started by our [Environment],
 * or [startCoroutineCancellable] runners.
 * When Arrow Fx Coroutines code is started by KotlinX runners, then the Arrow Fx Coroutines KotlinX Coroutines module must install a [SuspendConnection]
 * for the operation to be cancellable, and thus we'd find a connection here as well.
 */
internal fun CoroutineContext.connection(): SuspendConnection =
  this[SuspendConnection] ?: SuspendConnection.uncancellable

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
@PublishedApi
internal sealed class SuspendConnection : AbstractCoroutineContextElement(SuspendConnection) {

  abstract suspend fun cancel(): Unit
  fun cancelToken(): CancelToken = CancelToken { cancel() }

  abstract fun isCancelled(): Boolean
  abstract fun push(tokens: List<CancelToken>): Unit
  fun isNotCancelled(): Boolean = !isCancelled()

  abstract fun push(token: CancelToken): Unit

  fun pushPair(lh: SuspendConnection, rh: SuspendConnection): Unit =
    pushPair(lh.cancelToken(), rh.cancelToken())

  fun pushPair(lh: CancelToken, rh: CancelToken): Unit =
    push(listOf(lh, rh))

  abstract fun pop(): CancelToken
  abstract fun tryReactivate(): Boolean

  fun toDisposable(): Disposable = {
    Platform.unsafeRunSync { cancel() }
  }

  companion object Key : CoroutineContext.Key<SuspendConnection> {
    val uncancellable: SuspendConnection = Uncancellable
    operator fun invoke(): SuspendConnection = DefaultConnection()
  }

  object Uncancellable : SuspendConnection() {
    override suspend fun cancel() = Unit
    override fun isCancelled(): Boolean = false
    override fun push(tokens: List<CancelToken>) = Unit
    override fun push(token: CancelToken) = Unit
    override fun pop(): CancelToken = CancelToken.unit
    override fun tryReactivate(): Boolean = true
    override fun toString(): String = "UncancellableConnection"
  }

  class DefaultConnection : SuspendConnection() {
    private val state: AtomicRef<List<CancelToken>?> = atomic(emptyList())

    override suspend fun cancel(): Unit =
      state.getAndSet(null).let { stack ->
        when {
          stack == null || stack.isEmpty() -> CancelToken.unit
          else -> stack.cancelAll()
        }
      }

    override fun isCancelled(): Boolean = state.value == null

    override tailrec fun push(token: CancelToken): Unit = when (val list = state.value) {
      // If connection is already cancelled cancel token immediately.
      null -> token.cancel
        .startCoroutine(Continuation(EmptyCoroutineContext) { })
      else ->
        if (state.compareAndSet(list, listOf(token) + list)) Unit
        else push(token)
    }

    override fun push(tokens: List<CancelToken>) =
      push(tokens.asSingleToken())

    override tailrec fun pop(): CancelToken {
      val state = state.value
      return when {
        state == null || state.isEmpty() -> CancelToken.unit
        else ->
          if (this.state.compareAndSet(state, state.drop(1))) state.first()
          else pop()
      }
    }

    override fun tryReactivate(): Boolean =
      state.compareAndSet(null, emptyList())

    private suspend fun List<CancelToken>.cancelAll(): Unit =
      forEach { it.invoke() }

    private fun List<CancelToken>.asSingleToken(): CancelToken =
      CancelToken { cancelAll() }

    override fun toString(): String =
      "SuspendConnection(isCancelled = ${isCancelled()}, size= ${state.value?.size ?: 0})"
  }
}
