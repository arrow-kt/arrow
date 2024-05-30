package arrow

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Builds a [CoroutineScope] on top of the [FinallyScope] DSL,
 * this gives the same guarantees as:
 * ```kotlin
 * coroutineScope {
 *
 * }
 * ```
 * but in a flattened imperative way, some use-cases.
 * ```kotlin
 * context(Scope, AwaitScope)
 * fun parallel() = scope {
 *   (0..10)
 *     .map { coroutineScope() }
 *     .map { scope.async { crazyness() } }
 * }
 * ```
 *
 * ```kotlin
 * context(Scope)
 * fun kafkaConsumer(): KConsumer =
 *   KConsumer(coroutineScope(), settings)
 * ```
 */
public suspend fun FinallyScope.coroutineScope(context: CoroutineContext): CoroutineScope {
  val oldContext = currentCoroutineContext()
  val newContext = currentCoroutineContext() + context
  val newJob = newContext[Job] ?: Job(oldContext[Job])
  val scope = CoroutineScope(newContext + newJob)
  finalise { e ->
    when (e) {
      null -> newJob.join()
      else -> try {
        newJob.cancelAndJoin()
      } catch (e: CancellationException) {
        newJob.cancel(e)
        newJob.join()
      }
    }
  }
  return scope
}
