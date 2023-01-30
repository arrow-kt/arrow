@file:JvmMultifileClass
@file:JvmName("ParTraverse")

package arrow.fx.coroutines

import arrow.core.continuations.result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
@Deprecated(
  "Prefer composing parMap with result DSL",
  ReplaceWith(
    "result<List<B>> { this.parMap(Dispatchers.Default, n) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.result",
    "kotlinx.coroutines.Dispatchers"
  )
)
@JvmName("parSequenceResultNScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> Result<A>>.parSequenceResultN(n: Int): Result<List<A>> =
  result { parMap(Dispatchers.Default, n) { it().bind() } }

@Deprecated(
  "Prefer composing parMap with result DSL",
  ReplaceWith(
    "result<List<B>> { this.parMap(Dispatchers.Default, n) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.result",
    "kotlinx.coroutines.Dispatchers"
  )
)
public suspend fun <A> Iterable<suspend () -> Result<A>>.parSequenceResultN(n: Int): Result<List<A>> =
  result { parMap(Dispatchers.Default, n) { it().bind() } }

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
@Deprecated(
  "Prefer composing parMap with result DSL",
  ReplaceWith(
    "result<List<B>> { this.parMap(ctx, n) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.result"
  )
)
@JvmName("parSequenceResultNScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> Result<A>>.parSequenceResultN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int
): Result<List<A>> =
  result { parMap(ctx, n) { it().bind() } }

@Deprecated(
  "Prefer composing parMap with result DSL",
  ReplaceWith(
    "result<List<B>> { this.parMap(ctx, n) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.result"
  )
)
public suspend fun <A> Iterable<suspend () -> Result<A>>.parSequenceResultN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int
): Result<List<A>> =
  result { parMap(ctx, n) { it().bind() } }

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [Dispatchers.Default].
 * If one or more of the [f] returns [Result.failure] then all the [Result.failure] results will be combined using [addSuppressed].
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Prefer composing parMap with result DSL",
  ReplaceWith(
    "result<List<B>> { this.parMap(Dispatchers.Default, n) { f(it).bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.result",
    "kotlinx.coroutines.Dispatchers"
  )
)
public suspend fun <A, B> Iterable<A>.parTraverseResultN(
  n: Int,
  f: suspend CoroutineScope.(A) -> Result<B>
): Result<List<B>> =
  result { parMap(Dispatchers.Default, n) { f(it).bind() } }

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
@Deprecated(
  "Prefer composing parMap with result DSL",
  ReplaceWith(
    "result<List<B>> { this.parMap(ctx, n) { f(it).bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.result"
  )
)
public suspend fun <A, B> Iterable<A>.parTraverseResultN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  n: Int,
  f: suspend CoroutineScope.(A) -> Result<B>
): Result<List<B>> = result { parMap(ctx, n) { f(it).bind() } }

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
@Deprecated(
  "Prefer composing parMap with result DSL",
  ReplaceWith(
    "result<List<B>> { this.parMap(ctx) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.result"
  )
)
@JvmName("parSequenceResultScoped")
public suspend fun <A> Iterable<suspend CoroutineScope.() -> Result<A>>.parSequenceResult(
  ctx: CoroutineContext = EmptyCoroutineContext
): Result<List<A>> = result { parMap(ctx) { it().bind() } }

@Deprecated(
  "Prefer composing parMap with result DSL",
  ReplaceWith(
    "result<List<B>> { this.parMap(ctx) { it().bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.result"
  )
)
public suspend fun <A> Iterable<suspend () -> Result<A>>.parSequenceResult(
  ctx: CoroutineContext = EmptyCoroutineContext
): Result<List<A>> =
  result { parMap(ctx) { it().bind() } }

/**
 * Traverses this [Iterable] and runs all mappers [f] on [Dispatchers.Default].
 * If one or more of the [f] returns [Result.failure] then all the [Result.failure] results will be combined using [addSuppressed].
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Prefer composing parMap with result DSL",
  ReplaceWith(
    "result<List<B>> { this.parMap(Dispatchers.Default) { f(it).bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.result",
    "kotlinx.coroutines.Dispatchers"
  )
)
public suspend fun <A, B> Iterable<A>.parTraverseResult(f: suspend CoroutineScope.(A) -> Result<B>): Result<List<B>> =
  result { parMap(Dispatchers.Default) { f(it).bind() } }

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
@Deprecated(
  "Prefer composing parMap with result DSL",
  ReplaceWith(
    "result<List<B>> { this.parMap(ctx) { f(it).bind() } }",
    "arrow.fx.coroutines.parMap",
    "arrow.core.continuations.result"
  )
)
public suspend fun <A, B> Iterable<A>.parTraverseResult(
  ctx: CoroutineContext = EmptyCoroutineContext,
  f: suspend CoroutineScope.(A) -> Result<B>
): Result<List<B>> =
  result { parMap(ctx) { f(it).bind() } }
