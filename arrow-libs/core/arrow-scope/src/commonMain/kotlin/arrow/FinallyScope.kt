package arrow

import arrow.atomic.Atomic
import arrow.atomic.update
import kotlin.coroutines.cancellation.CancellationException

public interface Finally : AutoCloseScope {
  public override fun autoClose(close: (Throwable?) -> Unit): Unit
  public fun finalise(block: suspend (Throwable?) -> Unit): Unit
}

public suspend fun <A> finally(block: Finally.() -> A): A {
  val scope = DefaultFinally()
  return try {
    block(scope)
  } catch (e: CancellationException) {
    scope.close(e) ?: throw e
  } catch (e: Throwable) {
    scope.close(e.throwIfFatal()) ?: throw e
  }
}

private class DefaultFinally : Finally {
  private val closeables: Atomic<List<suspend (Throwable?) -> Unit>> = Atomic(emptyList())

  suspend fun close(error: Throwable?): Nothing? {
    return closeables.get().asReversed().fold(error) { acc, function ->
      acc.add(runCatching { function(error) }.exceptionOrNull())
    }?.let { throw it }
  }

  override fun finalise(block: suspend (Throwable?) -> Unit): Unit =
    closeables.update {
      val closeLambda: suspend (Throwable?) -> Unit =
        { e: Throwable? -> close(e) }
      listOf(closeLambda) + it
    }

  override fun autoClose(close: (Throwable?) -> Unit) =
    finalise(close)
}

private fun Throwable?.add(other: Throwable?): Throwable? =
  this?.apply {
    other?.let { addSuppressed(it) }
  } ?: other

