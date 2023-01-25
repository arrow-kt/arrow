@file:JvmMultifileClass
@file:JvmName("ParTraverse")

package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.continuations.either
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(Dispatchers.Default, n) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either",
    "kotlinx.coroutines.Dispatchers"
  )
)
public suspend fun <A, B> Iterable<suspend () -> Either<A, B>>.parSequenceEitherN(n: Int): Either<A, List<B>> =
  either { parMap(Dispatchers.Default, n) { it().bind() } }

@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(Dispatchers.Default, n) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either",
    "kotlinx.coroutines.Dispatchers"
  )
)
@JvmName("parSequenceEitherNScoped")
public suspend fun <A, B> Iterable<suspend CoroutineScope.() -> Either<A, B>>.parSequenceEitherN(n: Int): Either<A, List<B>> =
  either { parMap(Dispatchers.Default, n) { it().bind() } }

/**
 * Sequences all tasks in [n] parallel processes on [ctx] and return the result.
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks
 */
@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(ctx, n) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either"
  )
)
@JvmName("parSequenceEitherNScoped")
public suspend fun <A, B> Iterable<suspend CoroutineScope.() -> Either<A, B>>.parSequenceEitherN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int
): Either<A, List<B>> =
  either { parMap(ctx, n) { it().bind() } }

@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(ctx, n) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either"
  )
)
public suspend fun <A, B> Iterable<suspend () -> Either<A, B>>.parSequenceEitherN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int
): Either<A, List<B>> =
  either { parMap(ctx, n) { it().bind() } }

/**
 * Sequences all tasks in parallel on [Dispatchers.Default] and return the result.
 * If one of the tasks returns [Either.Left], then it will short-circuit the operation and
 * cancelling all this running tasks, and returning the first encountered [Either.Left].
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(Dispatchers.Default) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either",
    "kotlinx.coroutines.Dispatchers"
  )
)
public suspend fun <A, B> Iterable<suspend () -> Either<A, B>>.parSequenceEither(): Either<A, List<B>> =
  either { parMap(Dispatchers.Default) { it().bind() } }

@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(Dispatchers.Default) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either",
    "kotlinx.coroutines.Dispatchers"
  )
)
@JvmName("parSequenceEitherScoped")
public suspend fun <A, B> Iterable<suspend CoroutineScope.() -> Either<A, B>>.parSequenceEither(): Either<A, List<B>> =
  either { parMap(Dispatchers.Default) { it().bind() } }

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
 * ```kotlin
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
 * <!--- KNIT example-partraverseeither-01.kt -->
 */
@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(ctx) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either"
  )
)
@JvmName("parSequenceEitherScoped")
public suspend fun <A, B> Iterable<suspend CoroutineScope.() -> Either<A, B>>.parSequenceEither(
  ctx: CoroutineContext = EmptyCoroutineContext
): Either<A, List<B>> = either { parMap(ctx) { it().bind() } }

@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(ctx) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either"
  )
)
public suspend fun <A, B> Iterable<suspend () -> Either<A, B>>.parSequenceEither(
  ctx: CoroutineContext = EmptyCoroutineContext
): Either<A, List<B>> = either { parMap(ctx) { it().bind() } }

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [Dispatchers.Default].
 * If one of the [f] returns [Either.Left], then it will short-circuit the operation and
 * cancelling all this running [f], and returning the first encountered [Either.Left].
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(Dispatchers.Default, n) { f(it).bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either",
    "kotlinx.coroutines.Dispatchers"
  )
)
public suspend fun <A, B, E> Iterable<A>.parTraverseEitherN(
  n: Int,
  f: suspend CoroutineScope.(A) -> Either<E, B>
): Either<E, List<B>> = either { parMap(Dispatchers.Default, n) { f(it).bind() } }

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
@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(ctx, n) { f(it).bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either"
  )
)
public suspend fun <A, B, E> Iterable<A>.parTraverseEitherN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int,
  f: suspend CoroutineScope.(A) -> Either<E, B>
): Either<E, List<B>> = either { parMap(ctx, n) { f(it).bind() } }

/**
 * Traverses this [Iterable] and runs all mappers [f] on [Dispatchers.Default].
 * If one of the [f] returns [Either.Left], then it will short-circuit the operation and
 * cancelling all this running [f], and returning the first encountered [Either.Left].
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(Dispatchers.Default) { f(it).bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either",
    "kotlinx.coroutines.Dispatchers"
  )
)
public suspend fun <A, B, E> Iterable<A>.parTraverseEither(
  f: suspend CoroutineScope.(A) -> Either<E, B>
): Either<E, List<B>> =
  either { parMap(Dispatchers.Default) { f(it).bind() } }

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
 * ```kotlin
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
 *     .parTraverseEither(Dispatchers.IO) { getUserById(it) }
 *
 *   val res2 = listOf(1, 4, 2, 3)
 *     .parTraverseEither(Dispatchers.IO) { getUserById(it) }
 *  //sampleEnd
 *  println(res)
 *  println(res2)
 * }
 * ```
 * <!--- KNIT example-partraverseeither-02.kt -->
 */
@Deprecated(
  "Prefer composing parMap with either DSL",
  ReplaceWith(
    "either<E, List<B>> { this.parMap(ctx) { f(it).bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.either"
  )
)
public suspend fun <A, B, E> Iterable<A>.parTraverseEither(
  ctx: CoroutineContext = EmptyCoroutineContext,
  f: suspend CoroutineScope.(A) -> Either<E, B>
): Either<E, List<B>> =
  either { parMap(ctx) { f(it).bind() } }
