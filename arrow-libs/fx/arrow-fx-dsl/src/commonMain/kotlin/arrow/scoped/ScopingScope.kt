// Import throwIfFatal from AutoCloseScope
@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package arrow.scoped

import arrow.AutoCloseScope
import arrow.throwIfFatal
import arrow.atomic.Atomic
import arrow.atomic.update
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException

public interface ScopingScope : AutoCloseScope {
  public override fun autoClose(close: (Throwable?) -> Unit): Unit
  public fun closing(block: suspend (Throwable?) -> Unit): Unit
}

public suspend fun <A> scoped(block: suspend ScopingScope.() -> A): A {
  val scope = DefaultScopingScope()
  return try {
    block(scope)
  } catch (e: CancellationException) {
    scope.close(e) ?: throw e
  } catch (e: Throwable) {
    scope.close(e.throwIfFatal()) ?: throw e
  }
}

private class DefaultScopingScope : ScopingScope {
  private val closeables: Atomic<List<suspend (Throwable?) -> Unit>> = Atomic(emptyList())

  suspend fun close(error: Throwable?): Nothing? {
    return withContext(NonCancellable) {
      closeables.get().asReversed().fold(error) { acc, function ->
        acc.add(runCatching { function(error) }.exceptionOrNull())
      }?.let { throw it }
    }
  }

  override fun closing(block: suspend (Throwable?) -> Unit): Unit =
    closeables.update {
      val closeLambda: suspend (Throwable?) -> Unit =
        { e: Throwable? -> close(e) }
      listOf(closeLambda) + it
    }

  override fun autoClose(close: (Throwable?) -> Unit) =
    closing(close)
}

private fun Throwable?.add(other: Throwable?): Throwable? =
  this?.apply {
    other?.let { addSuppressed(it) }
  } ?: other

