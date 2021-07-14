@file:JvmMultifileClass
@file:JvmName("ParTraverse")
package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.computations.either
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
public suspend fun <A, B> Iterable<suspend () -> Either<A, B>>.parSequenceEitherN(n: Int): Either<A, List<B>> =
  parTraverseEitherN(Dispatchers.Default, n) { it() }

/**
 * Sequences all tasks in [n] parallel processes on [ctx] and return the result.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks
 */
public suspend fun <A, B> Iterable<suspend () -> Either<A, B>>.parSequenceEitherN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int
): Either<A, List<B>> =
  parTraverseEitherN(ctx, n) { it() }

/**
 * Sequences all tasks in parallel on [Dispatchers.Default] and return the result.
 * If one of the tasks returns [Either.Left], then it will short-circuit the operation and
 * cancelling all this running tasks, and returning the first encountered [Either.Left].
 *
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B> Iterable<suspend () -> Either<A, B>>.parSequenceEither(): Either<A, List<B>> =
  parTraverseEither(Dispatchers.Default) { it() }

/**
 * Sequences all tasks in parallel on [ctx] and return the result.
 * If one of the tasks returns [Either.Left], then it will short-circuit the operation and
 * cancelling all this running tasks, and returning the first encountered [Either.Left].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * object Error
 * typealias Task = suspend () -> Either<Throwable, Unit>
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   fun getTask(id: Int): Task =
 *     suspend { Either.catch { println("Working on task $id on ${Thread.currentThread().name}") } }
 *
 *   val res = listOf(1, 2, 3)
 *     .map(::getTask)
 *     .parSequenceEither(Dispatchers.IO)
 *   //sampleEnd
 *   println(res)
 * }
 * ```
 */
public suspend fun <A, B> Iterable<suspend () -> Either<A, B>>.parSequenceEither(
  ctx: CoroutineContext = EmptyCoroutineContext
): Either<A, List<B>> =
  parTraverseEither(ctx) { it() }

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [Dispatchers.Default].
 * If one of the [f] returns [Either.Left], then it will short-circuit the operation and
 * cancelling all this running [f], and returning the first encountered [Either.Left].
 *
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B, E> Iterable<A>.parTraverseEitherN(
  n: Int,
  f: suspend (A) -> Either<E, B>
): Either<E, List<B>> =
  parTraverseEitherN(Dispatchers.Default, n, f)

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [Dispatchers.Default].
 * If one of the [f] returns [Either.Left], then it will short-circuit the operation and
 * cancelling all this running [f], and returning the first encountered [Either.Left].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B, E> Iterable<A>.parTraverseEitherN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int,
  f: suspend (A) -> Either<E, B>
): Either<E, List<B>> {
  val semaphore = Semaphore(n)
  return parTraverseEither(ctx) { a ->
    semaphore.withPermit { f(a) }
  }
}

/**
 * Traverses this [Iterable] and runs all mappers [f] on [Dispatchers.Default].
 * If one of the [f] returns [Either.Left], then it will short-circuit the operation and
 * cancelling all this running [f], and returning the first encountered [Either.Left].
 *
 * Cancelling this operation cancels all running tasks.
 */
public suspend fun <A, B, E> Iterable<A>.parTraverseEither(
  f: suspend (A) -> Either<E, B>
): Either<E, List<B>> =
  parTraverseEither(Dispatchers.Default, f)

/**
 * Traverses this [Iterable] and runs all mappers [f] on [CoroutineContext].
 * If one of the [f] returns [Either.Left], then it will short-circuit the operation and
 * cancelling all this running [f], and returning the first encountered [Either.Left].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * object Error
 * data class User(val id: Int, val createdOn: String)
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   suspend fun getUserById(id: Int): Either<Error, User> =
 *     if(id == 4) Error.left()
 *     else User(id, Thread.currentThread().name).right()
 *
 *   val res = listOf(1, 2, 3)
 *     .parTraverseEither(Dispatchers.IO, ::getUserById)
 *
 *   val res2 = listOf(1, 4, 2, 3)
 *     .parTraverseEither(Dispatchers.IO, ::getUserById)
 *  //sampleEnd
 *  println(res)
 *  println(res2)
 * }
 * ```
 */
public suspend fun <A, B, E> Iterable<A>.parTraverseEither(
  ctx: CoroutineContext = EmptyCoroutineContext,
  f: suspend (A) -> Either<E, B>
): Either<E, List<B>> =
  either {
    coroutineScope {
      map { async(ctx) { f.invoke(it).bind() } }.awaitAll()
    }
  }
