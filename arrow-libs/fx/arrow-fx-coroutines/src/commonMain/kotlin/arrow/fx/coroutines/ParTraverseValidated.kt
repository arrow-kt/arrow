@file:JvmMultifileClass
@file:JvmName("ParTraverse")

package arrow.fx.coroutines

import arrow.core.Validated
import arrow.typeclasses.Semigroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [CoroutineContext].
 * If one or more of the tasks returns [Validated.Invalid] then all the [Validated.Invalid] results will be combined using [semigroup].
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<B>>>(semigroup) { this.parMapOrAccumulate(Dispatchers.Default, n, { a, b -> a.combine(b) }) { it.invoke().bind() }.toValidated() }",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "kotlinx.coroutines.Dispatchers",
    "arrow.core.Validated"
  )
)
@JvmName("parSequenceValidatedNScoped")
public suspend fun <E, A> Iterable<suspend CoroutineScope.() -> Validated<E, A>>.parSequenceValidatedN(
  semigroup: Semigroup<E>,
  n: Int
): Validated<E, List<A>> = with(semigroup) {
  parMapOrAccumulate(Dispatchers.Default, n, { a, b -> a.combine(b) }) { it().bind() }.toValidated()
}

@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<B>>>(semigroup) { this.parMapOrAccumulate(Dispatchers.Default, n, { a, b -> a.combine(b) }) { it.invoke().bind() }.toValidated() }",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "kotlinx.coroutines.Dispatchers",
    "arrow.core.Validated"
  )
)
public suspend fun <E, A> Iterable<suspend () -> Validated<E, A>>.parSequenceValidatedN(
  semigroup: Semigroup<E>,
  n: Int
): Validated<E, List<A>> = with(semigroup) {
  parMapOrAccumulate(Dispatchers.Default, n, { a, b -> a.combine(b) }) { it().bind() }.toValidated()
}

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [CoroutineContext].
 * If one or more of the tasks returns [Validated.Invalid] then all the [Validated.Invalid] results will be combined using [semigroup].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<B>>>(semigroup) { this.parMapOrAccumulate(ctx, n, { a, b -> a.combine(b) }) { it.invoke().bind() }.toValidated() }",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "arrow.core.Validated"
  )
)
@JvmName("parSequenceValidatedNScoped")
public suspend fun <E, A> Iterable<suspend CoroutineScope.() -> Validated<E, A>>.parSequenceValidatedN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  semigroup: Semigroup<E>,
  n: Int
): Validated<E, List<A>> = with(semigroup) {
  parMapOrAccumulate(ctx, n, { a, b -> a.combine(b) }) { it().bind() }.toValidated()
}

@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<B>>>(semigroup) { this.parMapOrAccumulate(ctx, n, { a, b -> a.combine(b) }) { it.invoke().bind() }.toValidated()",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "arrow.core.Validated"
  )
)
public suspend fun <E, A> Iterable<suspend () -> Validated<E, A>>.parSequenceValidatedN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  semigroup: Semigroup<E>,
  n: Int
): Validated<E, List<A>> = with(semigroup) {
  parMapOrAccumulate(ctx, n, { a, b -> a.combine(b) }) { it().bind() }.toValidated()
}

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [Dispatchers.Default].
 * If one or more of the [f] returns [Validated.Invalid] then all the [Validated.Invalid] results will be combined using [semigroup].
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<B>>>(semigroup) { this.parMapOrAccumulate(Dispatchers.Default, n, { a, b -> a.combine(b) }) { f(it).bind() }.toValidated() }",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "kotlinx.coroutines.Dispatchers",
    "arrow.core.Validated"
  )
)
public suspend fun <E, A, B> Iterable<A>.parTraverseValidatedN(
  semigroup: Semigroup<E>,
  n: Int,
  f: suspend CoroutineScope.(A) -> Validated<E, B>
): Validated<E, List<B>> = with(semigroup) {
  parMapOrAccumulate(Dispatchers.Default, n, { a, b -> a.combine(b) }) { f(it).bind() }.toValidated()
}

/**
 * Traverses this [Iterable] and runs [f] in [n] parallel operations on [CoroutineContext].
 * If one or more of the [f] returns [Validated.Invalid] then all the [Validated.Invalid] results will be combined using [semigroup].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<B>>>(semigroup) { this.parMapOrAccumulate(Dispatchers.Default, n, { a, b -> a.combine(b) }) { f(it).bind() }.toValidated() }",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "kotlinx.coroutines.Dispatchers",
    "arrow.core.Validated"
  )
)
public suspend fun <E, A, B> Iterable<A>.parTraverseValidatedN(
  ctx: CoroutineContext = EmptyCoroutineContext,
  semigroup: Semigroup<E>,
  n: Int,
  f: suspend CoroutineScope.(A) -> Validated<E, B>
): Validated<E, List<B>> = with(semigroup) {
  parMapOrAccumulate(ctx, n, { a, b -> a.combine(b) }) { f(it).bind() }.toValidated()
}

/**
 * Sequences all tasks in parallel on [Dispatchers.Default] and returns the result.
 * If one or more of the tasks returns [Validated.Invalid] then all the [Validated.Invalid] results will be combined using [semigroup].
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<B>>>(semigroup) { this.parMapOrAccumulate(Dispatchers.Default, { a, b -> a.combine(b) }) { it.invoke().bind() }.toValidated() }",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "kotlinx.coroutines.Dispatchers",
    "arrow.core.Validated"
  )
)
@JvmName("parSequenceValidatedScoped")
public suspend fun <E, A> Iterable<suspend CoroutineScope.() -> Validated<E, A>>.parSequenceValidated(semigroup: Semigroup<E>): Validated<E, List<A>> =
  with(semigroup) {
    parMapOrAccumulate(Dispatchers.Default, { a, b -> a.combine(b) }) { it().bind() }.toValidated()
  }

@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<B>>>(semigroup) { this.parMapOrAccumulate(Dispatchers.Default, { a, b -> a.combine(b) }) { it.invoke().bind() }.toValidated() }",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "kotlinx.coroutines.Dispatchers",
    "arrow.core.Validated"
  )
)
public suspend fun <E, A> Iterable<suspend () -> Validated<E, A>>.parSequenceValidated(semigroup: Semigroup<E>): Validated<E, List<A>> =
  with(semigroup) {
    parMapOrAccumulate(Dispatchers.Default, { a, b -> a.combine(b) }) { it().bind() }.toValidated()
  }

/**
 * Sequences all tasks in parallel on [ctx] and returns the result.
 * If one or more of the tasks returns [Validated.Invalid] then all the [Validated.Invalid] results will be combined using [semigroup].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin
 * import arrow.core.*
 * import arrow.typeclasses.Semigroup
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * typealias Task = suspend () -> ValidatedNel<Throwable, Unit>
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   fun getTask(id: Int): Task =
 *     suspend { Validated.catchNel { println("Working on task $id on ${Thread.currentThread().name}") } }
 *
 *   val res = listOf(1, 2, 3)
 *     .map(::getTask)
 *     .parSequenceValidated(Dispatchers.IO, Semigroup.nonEmptyList())
 *   //sampleEnd
 *   println(res)
 * }
 * ```
 * <!--- KNIT example-partraversevalidated-01.kt -->
 */
@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<B>>>(semigroup) { this.parMapOrAccumulate(ctx, { a, b -> a.combine(b) }) { it.invoke().bind() }.toValidated() }",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "arrow.core.Validated"
  )
)
@JvmName("parSequenceValidatedScoped")
public suspend fun <E, A> Iterable<suspend CoroutineScope.() -> Validated<E, A>>.parSequenceValidated(
  ctx: CoroutineContext = EmptyCoroutineContext,
  semigroup: Semigroup<E>
): Validated<E, List<A>> = with(semigroup) {
  parMapOrAccumulate(ctx, { a, b -> a.combine(b) }) { it().bind() }.toValidated()
}

@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<A>>>(semigroup) { this.parMapOrAccumulate(ctx, { a, b -> a.combine(b) }) { it.invoke().bind() }.toValidated() }",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "arrow.core.Validated"
  )
)
public suspend fun <E, A> Iterable<suspend () -> Validated<E, A>>.parSequenceValidated(
  ctx: CoroutineContext = EmptyCoroutineContext,
  semigroup: Semigroup<E>
): Validated<E, List<A>> = with(semigroup) {
  parMapOrAccumulate(ctx, { a, b -> a.combine(b) }) { it().bind() }.toValidated()
}

/**
 * Traverses this [Iterable] and runs all mappers [f] on [Dispatchers.Default].
 * If one or more of the [f] returns [Validated.Invalid] then all the [Validated.Invalid] results will be combined using [semigroup].
 *
 * Cancelling this operation cancels all running tasks.
 */
@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<B>>>(semigroup) { this.parMapOrAccumulate(Dispatchers.Default, { a, b -> a.combine(b) }) { f(it).bind() }.toValidated() }",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "kotlinx.coroutines.Dispatchers",
    "arrow.core.Validated"
  )
)
public suspend fun <E, A, B> Iterable<A>.parTraverseValidated(
  semigroup: Semigroup<E>,
  f: suspend CoroutineScope.(A) -> Validated<E, B>
): Validated<E, List<B>> = with(semigroup) {
  parMapOrAccumulate(Dispatchers.Default, { a, b -> a.combine(b) }) { f(it).bind() }.toValidated()
}

/**
 * Traverses this [Iterable] and runs all mappers [f] on [CoroutineContext].
 * If one or more of the [f] returns [Validated.Invalid] then all the [Validated.Invalid] results will be combined using [semigroup].
 *
 * Coroutine context is inherited from a [CoroutineScope], additional context elements can be specified with [ctx] argument.
 * If the combined context does not have any dispatcher nor any other [ContinuationInterceptor], then [Dispatchers.Default] is used.
 * **WARNING** If the combined context has a single threaded [ContinuationInterceptor], this function will not run in parallel.
 *
 * Cancelling this operation cancels all running tasks.
 *
 * ```kotlin
 * import arrow.core.*
 * import arrow.typeclasses.Semigroup
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.Dispatchers
 *
 * object Error
 * data class User(val id: Int, val createdOn: String)
 *
 * suspend fun main(): Unit {
 *   //sampleStart
 *   suspend fun getUserById(id: Int): ValidatedNel<Error, User> =
 *     if(id % 2 == 0) Error.invalidNel()
 *     else User(id, Thread.currentThread().name).validNel()
 *
 *   val res = listOf(1, 3, 5)
 *     .parTraverseValidated(Dispatchers.IO, Semigroup.nonEmptyList()) { getUserById(it) }
 *
 *   val res2 = listOf(1, 2, 3, 4, 5)
 *     .parTraverseValidated(Dispatchers.IO, Semigroup.nonEmptyList()) { getUserById(it) }
 *  //sampleEnd
 *  println(res)
 *  println(res2)
 * }
 * ```
 * <!--- KNIT example-partraversevalidated-02.kt -->
 */
@Deprecated(
  "Prefer using more generic parMapOrAccumulate. Remove the semigroup parameter if using NonEmptyList, or concrete combine function otherwise.",
  ReplaceWith(
    "with<Semigroup<E>, Validated<E, List<B>>>(semigroup) { this.parMapOrAccumulate(ctx, { a, b -> a.combine(b) }) { f(it).bind() }.toValidated() }",
    "arrow.fx.coroutines.parMapOrAccumulate",
    "arrow.core.Validated"
  )
)
public suspend fun <E, A, B> Iterable<A>.parTraverseValidated(
  ctx: CoroutineContext = EmptyCoroutineContext,
  semigroup: Semigroup<E>,
  f: suspend CoroutineScope.(A) -> Validated<E, B>
): Validated<E, List<B>> = with(semigroup) {
  parMapOrAccumulate(ctx, { a, b -> a.combine(b) }) { f(it).bind() }.toValidated()
}
