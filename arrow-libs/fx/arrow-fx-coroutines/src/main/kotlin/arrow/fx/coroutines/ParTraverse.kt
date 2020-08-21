package arrow.fx.coroutines

import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Sequence all tasks in [n] parallel processes and return the result
 * Cancelling this operation cancels all running tasks.
 */
suspend fun <A> Iterable<suspend () -> A>.parSequenceN(n: Long): List<A> =
  parSequenceN(ComputationPool, n)

/**
 * Sequence all tasks in [n] parallel processes and return the result
 * Cancelling this operation cancels all running tasks.
 *
 * **WARNING** it runs in parallel depending on the capabilities of the provided [CoroutineContext].
 * We ensure they start in sequence so it's guaranteed to finish on a single threaded context.
 *
 * @see parSequence for a function that ensures it runs in parallel on the [ComputationPool].
 */
suspend fun <A> Iterable<suspend () -> A>.parSequenceN(ctx: CoroutineContext, n: Long): List<A> {
  val s = Semaphore(n)
  return parTraverse(ctx) {
    s.withPermit { it.invoke() }
  }
}

/**
 * Sequence all tasks in parallel and return the result
 * Cancelling this operation cancels all running tasks.
 */
suspend fun <A> Iterable<suspend () -> A>.parSequence(): List<A> =
  parTraverse { it.invoke() }

/**
 * Sequence all tasks in parallel and return the result
 * Cancelling this operation cancels all running tasks.
 *
 * **WARNING** it runs in parallel depending on the capabilities of the provided [CoroutineContext].
 * We ensure they start in sequence so it's guaranteed to finish on a single threaded context.
 *
 * @see parSequence for a function that ensures it runs in parallel on the [ComputationPool].
 */
suspend fun <A> Iterable<suspend () -> A>.parSequence(ctx: CoroutineContext): List<A> =
  parTraverse(ctx) { it.invoke() }

/**
 * Traverse this [Iterable] and run mapper [f] in [n] parallel processes.
 * Cancelling this operation cancels all running tasks.
 */
suspend fun <A, B> Iterable<A>.parTraverseN(n: Long, f: suspend (A) -> B): List<B> {
  val s = Semaphore(n)
  return parTraverse(ComputationPool) { a ->
    s.withPermit { f(a) }
  }
}

/**
 * Traverse this [Iterable] and run [f] in parallel on [ctx].
 * Cancelling this operation cancels all running tasks.
 *
 * **WARNING** it runs in parallel depending on the capabilities of the provided [CoroutineContext].
 * We ensure they start in sequence so it's guaranteed to finish on a single threaded context.
 *
 * @see parTraverseN for a function that ensures it runs in parallel on the [ComputationPool].
 */
suspend fun <A, B> Iterable<A>.parTraverseN(ctx: CoroutineContext, n: Long, f: suspend (A) -> B): List<B> {
  val s = Semaphore(n)
  return parTraverse(ctx) { a ->
    s.withPermit { f(a) }
  }
}

/**
 * Traverse this [Iterable] and and run all mappers [f] in parallel.
 * Cancelling this operation cancels all running tasks.
 */
suspend fun <A, B> Iterable<A>.parTraverse(f: suspend (A) -> B): List<B> =
  parTraverse(ComputationPool, f)

/**
 * Traverse this [Iterable] and and run all mappers [f] on [CoroutineContext].
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * data class User(val id: Int)
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   suspend fun getUserById(id: Int): User =
 *     User(id)
 *
 *   val res = listOf(1, 2, 3)
 *     .parTraverse(ComputationPool, ::getUserById)
 *  //sampleEnd
 *  println(res)
 * }
 * ```
 *
 * **WARNING** it runs in parallel depending on the capabilities of the provided [CoroutineContext].
 * We ensure they start in sequence so it's guaranteed to finish on a single threaded context.
 *
 * @see parTraverse for a function that ensures it runs in parallel on the [ComputationPool].
 */ // Foldable impl, more performant implementations can be done for other data types
suspend fun <A, B> Iterable<A>.parTraverse(ctx: CoroutineContext, f: suspend (A) -> B): List<B> =
  if (ctx === EmptyCoroutineContext || ctx[ContinuationInterceptor] == null) map { a -> f(a) }
  else toList().foldRight(suspend { emptyList<B>() }) { a, acc ->
    suspend {
      parMapN(ctx, { f(a) }, { acc.invoke() }) { a, b -> listOf(a) + b }
    }
  }.invoke()
