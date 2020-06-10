package arrow.fx.coroutines

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * Inline marker to mark a [CancelToken],
 * This allows for clearer APIs in functions that expect a [CancelToken] to be returned.
 */
@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class CancelToken(val cancel: suspend () -> Unit) {

  suspend fun invoke(): Unit = cancel.invoke()

  companion object {
    val unit = CancelToken { Unit }
  }
}

typealias Disposable = () -> Unit

internal fun CoroutineContext.connection(): SuspendConnection =
  this[SuspendConnection] ?: SuspendConnection.uncancellable

/**
 * SuspendConnection is a state-machine inside [CoroutineContext] that manages cancellation.
 * This could in the future also serve as a mechanism to collect debug information on running connections.
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
      null -> Platform.unsafeRunSync { token.invoke() }
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
