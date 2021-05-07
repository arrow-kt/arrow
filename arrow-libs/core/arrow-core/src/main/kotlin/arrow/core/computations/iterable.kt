package arrow.core.computations

import arrow.continuations.Effect
import arrow.continuations.generic.DelimitedScope

/**
 * Allows to iterate over the [Iterable]<A> receiver and reduce to a nullable result.
 *
 * Can be used to convert a number of items in an [Iterable]<A> skipping any non requested
 * ```kotlin
 * val result: String? = listOf(1, 2, 3).partialReduceOrNull {
 *    val a = next() // gets 1
 *    val b = next() // gets 2
 *    "$a $b"
 * }
 * result shouldBe "1 2"
 * ```
 *
 * It's possible to also skip a value in the iteration:
 * ```kotlin
 * val result: String? = listOf(1, 2, 3).partialReduceOrNull {
 *    val a = next() // gets 1
 *    dropNext() // drop 2
 *    val b = next() //gets 3
 *    "$a $b"
 * }
 * result shouldBe "1 3"
 * ```
 *
 * If you need to skip more than one item:
 * ```kotlin
 * val result: String? = listOf(1, 2, 3, 4).partialReduceOrNull {
 *    val a = next() // gets 1
 *    drop(2) // drops 2 and 3
 *    val b = next() // gets 4
 *    "$a $b"
 * }
 * result shouldBe "1 4"
 * ```
 *
 * You can use `cancel()` to end the iteration if a given condition is met:
 * ```kotlin
 * val result: String? = listOf(1, 2, 3, 4).partialReduceOrNull {
 *    val a = next() // gets 1
 *    if(a == 1) {
 *      cancel() // ends iteration
 *    }
 *    val b = next() // is never run
 *    "$a $b" // won't return either
 * }
 * result shouldBe null
 * ```
 */
suspend fun <A, R> Iterable<A>.partialReduceOrNull(
  block: suspend IterableReduceEffect<A, R?>.() -> R,
): R? = Effect.suspended(eff = { IterableReduceEffect(it, iterator()) }, f = block, just = { it })

class IterableReduceEffect<A, R>(
  private val delimitedScope: DelimitedScope<R?>,
  private val iterator: Iterator<A>,
) : Effect<R?> {

  /**
   * returns the next items in the current iteration if available.
   */
  suspend fun next(): A = iterator.takeIf { it.hasNext() }?.next() ?: cancel()

  /**
   * Drops the next item in the current iteration without returning the value.
   */
  suspend fun dropNext() {
    next()
  }

  /**
   * Drops [n] itemrs form the current iteration.
   */
  suspend fun drop(n: Int) = repeat(n) { dropNext() }

  /**
   * Cancels the current iteration returning null and skipping any further operations.
   */
  suspend fun <B> cancel(): B = control().shift(null)

  override fun control(): DelimitedScope<R?> = delimitedScope
}
