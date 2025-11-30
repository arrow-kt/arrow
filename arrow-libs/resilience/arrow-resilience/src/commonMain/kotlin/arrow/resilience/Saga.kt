package arrow.resilience

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.core.nonFatalOrThrow
import arrow.core.prependTo

@Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
@PublishedApi
internal class SagaBuilder(
  private val stack: Atomic<List<suspend () -> Unit>> = Atomic(emptyList())
) : SagaScope {

  @SagaDSLMarker
  override suspend fun <A> saga(
    action: suspend SagaActionStep.() -> A,
    compensation: suspend (A) -> Unit
  ): A = action(SagaActionStep).also { res ->
    stack.update(suspend { compensation(res) }::prependTo)
  }

  @Deprecated("Binary compatibility", level = DeprecationLevel.HIDDEN)
  @PublishedApi
  internal suspend fun totalCompensation() {
    stack
      .get()
      .fold<suspend () -> Unit, Throwable?>(null) { acc, finalizer ->
        try {
          finalizer()
          acc
        } catch (e: Throwable) {
          e.nonFatalOrThrow()
          acc?.apply { addSuppressed(e) } ?: e
        }
      }
      ?.let { throw it }
  }
}
