@file:JvmMultifileClass
@file:JvmName("ParTraverse")

package arrow.fx.coroutines

import arrow.core.continuations.result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Traverses this [Iterable] and runs `suspend CoroutineScope.() -> Result<A>` in [n] parallel operations on [CoroutineContext].
 * If one or more of the tasks returns [Result.failure] then all the [Result.failure] results will be combined using [addSuppressed].
 *
 * Cancelling this operation cancels all running tasks.
 */
@JvmName("parSequenceResultNScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> Result<A>>.parSequenceResultN(n: Int): Result<List<A>> =
  parTraverseResultN(Dispatchers.Default, n) { it() }

public suspend fun <A> Iterable<suspend () -> Result<A>>.parSequenceResultN(n: Int): Result<List<A>> =
  parTraverseResultN(Dispatchers.Default, n) { it() }

/**
 * Traverses this [Iterable] and runs `suspend CoroutineScope.() -> Result<A>` in [n] parallel operations on [CoroutineContext].
 * If one or more of the tasks returns [Result.failure] then all the [Result.failure] results will be combined using [addSuppressed].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 */
@JvmName("parSequenceResultNScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> Result<A>>.parSequenceResultN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int,
): Result<List<A>> =
  parTraverseResultN(ctx, n) { it() }

public suspend fun <A> Iterable<suspend () -> Result<A>>.parSequenceResultN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int,
): Result<List<A>> =
  parTraverseResultN(ctx, n) { it() }

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [Dispatchers.Default].
 * If one or more of the [f] returns [Result.failure] then all the [Result.failure] results will be combined using [addSuppressed].
 *
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B> Iterable<A>.parTraverseResultN(
  n: Int,
  f: suspend CoroutineScope.(A) -> Result<B>,
): Result<List<B>> =
  parTraverseResultN(Dispatchers.Default, n, f)

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [CoroutineContext].
 * If one or more of the [f] returns [Result.failure] then all the [Result.failure] results will be combined using [addSuppressed].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B> Iterable<A>.parTraverseResultN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int,
  f: suspend CoroutineScope.(A) -> Result<B>,
): Result<List<B>> {
  val semaphore = Semaphore(n)
  return parTraverseResult(ctx) { a ->
    semaphore.withPermit { f(a) }
  }
}

/**
 * Sequences all tasks in parallel on [ctx] and returns the result.
 * If one or more of the tasks returns [Result.failure] then all the [Result.failure] results will be combined using [addSuppressed].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 *
 *
 */
@JvmName("parSequenceResultScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> Result<A>>.parSequenceResult(
  ctx: CoroutineContext = EmptyCoroutineContext,
): Result<List<A>> = parTraverseResult(ctx) { it() }

public suspend fun <A> Iterable<suspend () -> Result<A>>.parSequenceResult(
  ctx: CoroutineContext = EmptyCoroutineContext,
): Result<List<A>> = parTraverseResult(ctx) { it() }

/**
 * Traverses this [Iterable] and runs all mappers [f] on [Dispatchers.Default].
 * If one or more of the [f] returns [Result.failure] then all the [Result.failure] results will be combined using [addSuppressed].
 *
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B> Iterable<A>.parTraverseResult(f: suspend CoroutineScope.(A) -> Result<B>): Result<List<B>> =
  parTraverseResult(Dispatchers.Default, f)

/**
 * Traverses this [Iterable] and runs all mappers [f] on [CoroutineContext].
 * If one or more of the [f] returns [Result.failure] then all the [Result.failure] results will be combined using [addSuppressed].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 *
 */
public suspend fun <A, B> Iterable<A>.parTraverseResult(
  ctx: CoroutineContext = EmptyCoroutineContext,
  f: suspend CoroutineScope.(A) -> Result<B>,
): Result<List<B>> =
  result {
    coroutineScope {
      map { async(ctx) { f.invoke(this, it).bind() } }.awaitAll()
    }
  }
