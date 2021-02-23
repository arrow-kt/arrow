package arrow.fx.coroutines

import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

/**
 * Inserts a cancellable boundary.
 *
 * In a cancellable environment, we need to add mechanisms to react when cancellation is triggered.
 * In a coroutine, a cancel boundary checks for the cancellation status; it does not allow the coroutine to keep executing in the case cancellation was triggered.
 * It is useful, for example, to cancel the continuation of a loop, as shown in this code snippet:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * //sampleStart
 * suspend fun forever(): Unit {
 *   while(true) {
 *     println("I am getting dizzy...")
 *     cancelBoundary() // cancellable computation loop
 *   }
 * }
 *
 * suspend fun main(): Unit {
 *   val fiber = ForkConnected {
 *     guaranteeCase({ forever() }) { exitCase ->
 *       println("forever finished with $exitCase")
 *     }
 *   }
 *   sleep(10.milliseconds)
 *   fiber.cancel()
 * }
 * ```
 */
@Deprecated(
  "Use coroutineContext.ensureActive()",
  ReplaceWith(
    "coroutineContext.ensureActive()",
    "kotlinx.coroutines.ensureActive",
    "kotlin.coroutines.coroutineContext"
  )
)
suspend fun cancelBoundary(): Unit =
  coroutineContext.ensureActive()
