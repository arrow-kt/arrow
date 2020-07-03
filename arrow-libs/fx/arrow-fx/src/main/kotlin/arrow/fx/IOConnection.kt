package arrow.fx

import arrow.fx.internal.JavaCancellationException
import arrow.fx.typeclasses.Disposable
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

enum class OnCancel {
  ThrowCancellationException, Silent;

  companion object {
    val CancellationException = ConnectionCancellationException
  }
}

object ConnectionCancellationException : JavaCancellationException("User cancellation")

/**
 * An UnhandledError occurs when `E` is raised during cancellation.
 * These errors `E` will be thrown as `UnhandledError` upon cancellation.
 */
class UnhandledError(val error: Any?) : Throwable(message = "Encountered UnhandledError during cancellation: $error") {
  override fun fillInStackTrace(): Throwable = this
}

internal class IOContext(val connection: IOConnection) : AbstractCoroutineContextElement(IOContext) {
  companion object Key : CoroutineContext.Key<IOContext>
}

internal sealed class IOConnection {

  abstract fun cancel(): IOOf<Unit>
  abstract fun isCancelled(): Boolean
  fun isNotCancelled(): Boolean = !isCancelled()
  abstract fun push(token: IOOf<Unit>): Unit
  abstract fun push(vararg token: IOOf<Unit>): Unit
  fun pushPair(lh: IOConnection, rh: IOConnection): Unit = push(lh.cancel(), rh.cancel())
  fun pushPair(lh: IOOf<Unit>, rh: IOOf<Unit>): Unit = push(lh, rh)
  abstract fun pop(): IOOf<Unit>
  abstract fun tryReactivate(): Boolean

  fun toDisposable(): Disposable = {
    cancel().fix().unsafeRunSync()
  }

  companion object {
    val uncancellable: IOConnection = Uncancellable
    operator fun invoke(): IOConnection = DefaultConnection()
  }
}

private object Uncancellable : IOConnection() {
  override fun cancel(): IOOf<Unit> = IO.unit
  override fun isCancelled(): Boolean = false
  override fun push(token: IOOf<Unit>): Unit = Unit
  override fun push(vararg token: IOOf<Unit>): Unit = Unit
  override fun pop(): IOOf<Unit> = IO.unit
  override fun tryReactivate(): Boolean = true
  override fun toString(): String = "UncancellableConnection"
}

private class DefaultConnection : IOConnection() {
  private val state: AtomicRef<List<IOOf<Unit>>?> = atomic(emptyList())

  override fun cancel(): IOOf<Unit> = IO.defer {
    state.getAndSet(null).let { stack ->
      when {
        stack == null || stack.isEmpty() -> IO.unit
        else -> stack.cancelAll()
      }
    }
  }

  override fun isCancelled(): Boolean = state.value == null

  override tailrec fun push(token: IOOf<Unit>): Unit = when (val list = state.value) {
    // If connection is already cancelled cancel token immediately.
    null -> token.rethrow.unsafeRunSync()
    else ->
      if (state.compareAndSet(list, listOf(token.rethrow) + list)) Unit
      else push(token)
  }

  override fun push(vararg token: IOOf<Unit>): Unit =
    push(token.toList().cancelAll())

  override tailrec fun pop(): IOOf<Unit> {
    val state = state.value
    return when {
      state == null || state.isEmpty() -> IO.unit
      else ->
        if (this.state.compareAndSet(state, state.drop(1))) state.first()
        else pop()
    }
  }

  override fun tryReactivate(): Boolean =
    state.compareAndSet(null, emptyList())

  private fun List<IOOf<Unit>>.cancelAll(): IOOf<Unit> = IO.defer {
    // TODO this blocks forever if any `CancelToken<F>` doesn't terminate. Requires `fork`/`start` to avoid.
    fold(IO.unit) { acc, f -> f.fix().flatMap { acc } }
  }

  override fun toString(): String = "IOConnection(state = ${state.value})"
}

internal val <A> IOOf<A>.rethrow: IO<A>
  get() = handleErrorWith { t -> IO.raiseError<Nothing>(t) }
