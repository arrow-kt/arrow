@file:JvmMultifileClass
@file:JvmName("parMap")

package arrow.fx.coroutines

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

public suspend fun <A> Iterable<suspend () -> A>.parSequenceN(ctx: CoroutineContext = EmptyCoroutineContext, n: Int): List<A> {
  val s = Semaphore(n)
  return parMap(ctx) {
    s.withPermit { it.invoke() }
  }
}

/**
 * Sequences all tasks in [n] parallel processes and return the result.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks
 */
@JvmName("parSequenceNScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> A>.parSequenceN(ctx: CoroutineContext = EmptyCoroutineContext, n: Int): List<A> {
  val s = Semaphore(n)
  return parMap(ctx) {
    s.withPermit { it.invoke(this) }
  }
}


public suspend fun <A> Iterable<suspend () -> A>.parSequence(ctx: CoroutineContext = EmptyCoroutineContext): List<A> =
  parMap(ctx) { it.invoke() }

/**
 * Sequences all tasks in parallel and return the result
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * typealias Task = suspend () -> Unit
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   fun getTask(id: Int): Task =
 *     suspend { println("Working on task $id on ${Thread.currentThread().name}") }
 *
 *   val res = listOf(1, 2, 3)
 *     .map(::getTask)
 *     .parSequence(Dispatchers.IO)
 *   //sampleEnd
 *   println(res)
 * }
 * ```
 * <!--- KNIT example-parMap-01.kt -->
 */
@JvmName("parSequenceScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> A>.parSequence(ctx: CoroutineContext = EmptyCoroutineContext): List<A> =
  parMap(ctx) { it.invoke(this) }

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [Dispatchers.Default].
 * Cancelling this operation cancels all running tasks.
 */

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [ctx].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B> Iterable<A>.parMapN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int,
  f: suspend CoroutineScope.(A) -> B
): List<B> {
  val s = Semaphore(n)
  return parMap(ctx) { a ->
    s.withPermit { f(a) }
  }
}

/**
 * Traverses this [Iterable] and runs all mappers [f] on [CoroutineContext].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * data class User(val id: Int, val createdOn: String)
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   suspend fun getUserById(id: Int): User =
 *     User(id, Thread.currentThread().name)
 *
 *   val res = listOf(1, 2, 3)
 *     .parMap(Dispatchers.IO) { getUserById(it) }
 *  //sampleEnd
 *  println(res)
 * }
 * ```
 * <!--- KNIT example-parMap-02.kt -->
 */
public suspend fun <A, B> Iterable<A>.parMap(
  ctx: CoroutineContext = EmptyCoroutineContext,
  f: suspend CoroutineScope.(A) -> B
): List<B> = coroutineScope {
  map { async(ctx) { f.invoke(this, it) } }.awaitAll()
}
