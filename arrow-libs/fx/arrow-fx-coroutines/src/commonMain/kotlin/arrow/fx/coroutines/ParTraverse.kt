@file:JvmMultifileClass
@file:JvmName("ParTraverse")

package arrow.fx.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith(
    "parMap(Dispatchers.Default, n) { it() }",
    "arrow.fx.coroutines.parMap",
    "kotlinx.coroutines.Dispatchers"
  )
)
public suspend fun <A> Iterable<suspend () -> A>.parSequenceN(n: Int): List<A> =
  parMap(Dispatchers.Default, n) { it() }

/**
 * Sequences all tasks in [n] parallel processes on [Dispatchers.Default] and return the result.
 *
 * Cancelling this operation cancels all running tasks
 */
@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith(
    "parMap(Dispatchers.Default, n) { it() }",
    "arrow.fx.coroutines.parMap",
    "kotlinx.coroutines.Dispatchers"
  )
)
@JvmName("parSequenceNScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> A>.parSequenceN(n: Int): List<A> =
  parMap(Dispatchers.Default, n) { it() }

@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith(
    "parMap(ctx, n) { it() }",
    "arrow.fx.coroutines.parMap"
  )
)
public suspend fun <A> Iterable<suspend () -> A>.parSequenceN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int
): List<A> = parMap(ctx, n) { it() }

/**
 * Sequences all tasks in [n] parallel processes and return the result.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks
 */
@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith(
    "parMap(ctx, n) { it() }",
    "arrow.fx.coroutines.parMap"
  )
)
@JvmName("parSequenceNScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> A>.parSequenceN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int
): List<A> = parMap(ctx, n) { it() }
@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith(
    "parMap(Dispatchers.Default) { it() }",
    "arrow.fx.coroutines.parMap",
    "kotlinx.coroutines.Dispatchers"
  )
)
public suspend fun <A> Iterable<suspend () -> A>.parSequence(): List<A> =
  parMap(Dispatchers.Default) { it() }

/**
 * Sequences all tasks in parallel on [Dispatchers.Default] and return the result
 *
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin
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
 * <!--- KNIT example-partraverse-01.kt -->
 */
@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith(
    "parMap(Dispatchers.Default) { it() }",
    "arrow.fx.coroutines.parMap",
    "kotlinx.coroutines.Dispatchers"
  )
)
@JvmName("parSequenceScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> A>.parSequence(): List<A> =
  parMap(Dispatchers.Default) { it() }

@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith(
    "parMap(ctx) { it() }",
    "arrow.fx.coroutines.parMap"
  )
)
public suspend fun <A> Iterable<suspend () -> A>.parSequence(ctx: CoroutineContext = EmptyCoroutineContext): List<A> =
  parMap(ctx) { it() }

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
 * <!--- KNIT example-partraverse-02.kt -->
 */
@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith(
    "parMap(ctx) { it() }",
    "arrow.fx.coroutines.parMap"
  )
)
@JvmName("parSequenceScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> A>.parSequence(ctx: CoroutineContext = EmptyCoroutineContext): List<A> =
  parMap(ctx) { it() }

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [Dispatchers.Default].
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith(
    "parMap(Dispatchers.Default, n, f)",
    "arrow.fx.coroutines.parMap",
    "kotlinx.coroutines.Dispatchers"
  )
)
public suspend fun <A, B> Iterable<A>.parTraverseN(n: Int, f: suspend CoroutineScope.(A) -> B): List<B> =
  parMap(Dispatchers.Default, n, f)

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [ctx].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith(
    "parMap(ctx, n, f)",
    "arrow.fx.coroutines.parMap"
  )
)
public suspend fun <A, B> Iterable<A>.parTraverseN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int,
  f: suspend CoroutineScope.(A) -> B
): List<B> = parMap(ctx, n, f)

/**
 * Traverses this [Iterable] and runs all mappers [f] on [Dispatchers.Default].
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin
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
 *     .parTraverse { getUserById(it) }
 *  //sampleEnd
 *  println(res)
 * }
 * ```
 * <!--- KNIT example-partraverse-03.kt -->
 */
@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith(
    "parMap(Dispatchers.Default, f)",
    "arrow.fx.coroutines.parMap",
    "kotlinx.coroutines.Dispatchers"
  )
)
public suspend fun <A, B> Iterable<A>.parTraverse(f: suspend CoroutineScope.(A) -> B): List<B> =
  parMap(Dispatchers.Default, f)

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
 *     .parTraverse(Dispatchers.IO) { getUserById(it) }
 *  //sampleEnd
 *  println(res)
 * }
 * ```
 * <!--- KNIT example-partraverse-04.kt -->
 */
@Deprecated(
  "Function is being renamed to parMap in 2.x.x",
  ReplaceWith("parMap(ctx, f)", "arrow.fx.coroutines.parMap")
)
public suspend fun <A, B> Iterable<A>.parTraverse(
  ctx: CoroutineContext = EmptyCoroutineContext,
  f: suspend CoroutineScope.(A) -> B
): List<B> = parMap(ctx, f)
