@file:JvmMultifileClass
@file:JvmName("ParTraverse")

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

/**
 * Sequences all tasks in [n] parallel processes on [Dispatchers.Default] and return the result.
 *
 * Cancelling this operation cancels all running tasks
 */
public suspend fun <A> Iterable<suspend () -> A>.parSequenceN(n: Int): List<A> =
  parSequenceN(Dispatchers.Default, n)

/**
 * Sequences all tasks in [n] parallel processes and return the result.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks
 */
public suspend fun <A> Iterable<suspend () -> A>.parSequenceN(ctx: CoroutineContext = EmptyCoroutineContext, n: Int): List<A> {
  val s = Semaphore(n)
  return parTraverse(ctx) {
    s.withPermit { it.invoke() }
  }
}

/**
 * Sequences all tasks in parallel on [Dispatchers.Default] and return the result
 *
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
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
 *     .parSequence()
 *   //sampleEnd
 *   println(res)
 * }
 * ```
 */
public suspend fun <A> Iterable<suspend () -> A>.parSequence(): List<A> =
  parSequence(Dispatchers.Default)

/**
 * Sequences all tasks in parallel and return the result
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin:ank:playground
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
 */
public suspend fun <A> Iterable<suspend () -> A>.parSequence(ctx: CoroutineContext = EmptyCoroutineContext): List<A> =
  parTraverse(ctx) { it.invoke() }

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [Dispatchers.Default].
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B> Iterable<A>.parTraverseN(n: Int, f: suspend (A) -> B): List<B> =
  parTraverseN(Dispatchers.Default, n, f)

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [ctx].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B> Iterable<A>.parTraverseN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int,
  f: suspend (A) -> B
): List<B> {
  val s = Semaphore(n)
  return parTraverse(ctx) { a ->
    s.withPermit { f(a) }
  }
}

/**
 * Traverses this [Iterable] and runs all mappers [f] on [Dispatchers.Default].
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * data class User(val id: Int, val createdOn: String)
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   suspend fun getUserById(id: Int): User =
 *     User(id, Thread.currentThread().name)
 *
 *   val res = listOf(1, 2, 3)
 *     .parTraverse(::getUserById)
 *  //sampleEnd
 *  println(res)
 * }
 * ```
 */
public suspend fun <A, B> Iterable<A>.parTraverse(f: suspend (A) -> B): List<B> =
  parTraverse(Dispatchers.Default, f)

/**
 * Traverses this [Iterable] and runs all mappers [f] on [CoroutineContext].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin:ank:playground
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
 *     .parTraverse(Dispatchers.IO, ::getUserById)
 *  //sampleEnd
 *  println(res)
 * }
 * ```
 */
public suspend fun <A, B> Iterable<A>.parTraverse(
  ctx: CoroutineContext = EmptyCoroutineContext,
  f: suspend (A) -> B
): List<B> = coroutineScope {
  map { async(ctx) { f.invoke(it) } }.awaitAll()
}
