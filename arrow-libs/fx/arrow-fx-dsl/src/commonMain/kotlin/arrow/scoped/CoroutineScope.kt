package arrow.scoped

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Builds a [CoroutineScope] on top of the [ScopingScope] DSL,
 * this gives the same guarantees as:
 * ```kotlin
 * coroutineScope {
 *
 * }
 * ```
 * but in a flattened imperative way, some use-cases.
 * ```kotlin
 * context(Scope, AwaitScope)
 * fun parallel() =
 *   (0..10)
 *     .map { coroutineScope() }
 *     .map { scope.async { crazy-ness() } }
 *     .map { it.await() }
 * ```
 *
 * ```kotlin
 * context(Scope)
 * fun kafkaConsumer(): KConsumer =
 *   KConsumer(coroutineScope(), settings)
 * ```
 */
public suspend fun ScopingScope.coroutineScope(): CoroutineScope =
  scope(currentCoroutineContext()) { Job(it) }

/**
 * Unlike [coroutineScope] a failure in a child,
 * will not result in the scope failing, nor cancelling any sibling jobs.
 */
public suspend fun ScopingScope.supervisorScope(): CoroutineScope =
  scope(currentCoroutineContext()) { SupervisorJob(it) }

private fun ScopingScope.scope(
  context: CoroutineContext,
  createJob: (parent: Job?) -> Job,
) : CoroutineScope {
  val newJob = createJob(context[Job])
  val scope = CoroutineScope(context + newJob)
  closing { e ->
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
