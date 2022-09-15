@file:JvmMultifileClass
@file:JvmName("parMap")

package arrow.fx.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
  parMapResultN(Dispatchers.Default, n) { it() }

public suspend fun <A> Iterable<suspend () -> Result<A>>.parSequenceResultN(n: Int): Result<List<A>> =
  parMapResultN(Dispatchers.Default, n) { it() }

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
  n: Int
): Result<List<A>> =
  parMapResultN(ctx, n) { it() }

public suspend fun <A> Iterable<suspend () -> Result<A>>.parSequenceResultN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int
): Result<List<A>> =
  parMapResultN(ctx, n) { it() }

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [Dispatchers.Default].
 * If one or more of the [f] returns [Result.failure] then all the [Result.failure] results will be combined using [addSuppressed].
 *
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B> Iterable<A>.parMapResultN(
  n: Int,
  f: suspend CoroutineScope.(A) -> Result<B>
): Result<List<B>> =
  parMapResultN(Dispatchers.Default, n, f)

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
public suspend fun <A, B> Iterable<A>.parMapResultN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int,
  f: suspend CoroutineScope.(A) -> Result<B>
): Result<List<B>> {
  val semaphore = Semaphore(n)
  return parMapResult(ctx) { a ->
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
  ctx: CoroutineContext = EmptyCoroutineContext
): Result<List<A>> = parMapResult(ctx) { it() }

//todo(#2728): @marc check if this is still valid after removing traverse
public suspend fun <A> Iterable<suspend () -> Result<A>>.parSequenceResult(
  ctx: CoroutineContext = EmptyCoroutineContext
): Result<List<A>> = parMapResult(ctx) { it() }

/**
 * Traverses this [Iterable] and runs all mappers [f] on [Dispatchers.Default].
 * If one or more of the [f] returns [Result.failure] then all the [Result.failure] results will be combined using [addSuppressed].
 *
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B> Iterable<A>.parMapResult(f: suspend CoroutineScope.(A) -> Result<B>): Result<List<B>> =
  parMapResult(Dispatchers.Default, f)

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
public suspend fun <A, B> Iterable<A>.parMapResult(
  ctx: CoroutineContext = EmptyCoroutineContext,
  f: suspend CoroutineScope.(A) -> Result<B>
): Result<List<B>> = TODO("(#2728): @marc check if this is still valid after removing traverse")
//  coroutineScope {
//    map { async(ctx) { f.invoke(this, it) } }.awaitAll().sequence()
//  }
