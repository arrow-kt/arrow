@file:OptIn(ExperimentalContracts::class)

package arrow.fx.coroutines

import arrow.AutoCloseScope
import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.core.nonFatalOrThrow
import arrow.core.prependTo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException

@DslMarker
public annotation class ScopeDSL

@DslMarker
public annotation class ResourceDSL

/**
 * [Resource] models resource allocation and releasing. It is especially useful when multiple resources that depend on each other need to be acquired and later released in reverse order.
 * The capability of _installing_ resources is called [ResourceScope], and [Resource] defines the value associating the `acquisition` step, and the `finalizer`.
 * [Resource] allocates and releases resources in a safe way that co-operates with Structured Concurrency, and KotlinX Coroutines.
 *
 * It is especially useful when multiple resources that depend on each other need to be acquired, and later released in reverse order.
 * Or when you want to compose other `suspend` functionality into resource safe programs, such as concurrency, parallelism or Arrow's Effect.
 *
 * Creating a [Resource] _value_ can be done using the [resource] function,
 * and running a program using [ResourceScope] can be done using [resourceScope], or [use].
 * Upon termination all finalizers are then guaranteed to run afterwards in reverse order of acquisition.
 *
 * The following program is **not-safe** because it is prone to leak `dataSource` and `userProcessor` when an exception, or cancellation signal occurs whilst using the service.
 *
 * ```kotlin
 * class UserProcessor {
 *   fun start(): Unit = println("Creating UserProcessor")
 *   fun shutdown(): Unit = println("Shutting down UserProcessor")
 * }
 *
 * class DataSource {
 *   fun connect(): Unit = println("Connecting dataSource")
 *   fun close(): Unit = println("Closed dataSource")
 * }
 *
 * class Service(val db: DataSource, val userProcessor: UserProcessor) {
 *   suspend fun processData(): List<String> = throw RuntimeException("I'm going to leak resources by not closing them")
 * }
 *
 * suspend fun main(): Unit {
 *   val userProcessor = UserProcessor().also { it.start() }
 *   val dataSource = DataSource().also { it.connect() }
 *   val service = Service(dataSource, userProcessor)
 *
 *   service.processData()
 *
 *   dataSource.close()
 *   userProcessor.shutdown()
 * }
 * ```
 * <!--- KNIT example-resource-01.kt -->
 *
 * If we were using Kotlin JVM, we might've relied on `Closeable` or `AutoCloseable` and rewritten our code to:
 *
 * <!--- INCLUDE
 * import java.io.Closeable
 *
 * class UserProcessor : Closeable {
 *   fun start(): Unit = println("Creating UserProcessor")
 *   override fun close(): Unit = println("Shutting down UserProcessor")
 * }
 *
 * class DataSource : Closeable {
 *   fun connect(): Unit = println("Connecting dataSource")
 *   override fun close(): Unit = println("Closed dataSource")
 * }
 *
 * class Service(val db: DataSource, val userProcessor: UserProcessor) {
 *   suspend fun processData(): List<String> = throw RuntimeException("I'm going to leak resources by not closing them")
 * }
 * -->
 * ```kotlin
 * suspend fun main(): Unit {
 *   UserProcessor().use { userProcessor ->
 *     userProcessor.start()
 *     DataSource().use { dataSource ->
 *       dataSource.connect()
 *       Service(dataSource, userProcessor).processData()
 *     }
 *   }
 * }
 * ```
 * <!--- KNIT example-resource-02.kt -->
 *
 * However, while we fixed closing of `UserProcessor` and `DataSource` there are issues still with this code:
 *   1. It requires implementing `Closeable` or `AutoCloseable`, only possible for Kotlin JVM, not available for Kotlin MPP
 *   2. Requires implementing interface, or wrapping external types with i.e. `class CloseableOf<A>(val type: A): Closeable`.
 *   3. Requires nesting of different resources in callback tree, not composable.
 *   4. Enforces `close` method name, renamed `UserProcessor#shutdown` to `close`
 *   5. Cannot run suspend functions upon _fun close(): Unit_.
 *   6. No exit signal, we don't know if we exited successfully, with an error or cancellation.
 *
 * [Resource] solves of these issues. It defines 3 different steps:
 *   1. Acquiring the resource of `A`.
 *   2. Using `A`.
 *   3. Releasing `A` with [ExitCase.Completed], [ExitCase.Failure] or [ExitCase.Cancelled].
 *
 * We rewrite our previous example to [Resource] below by:
 *  1. Define [Resource] for `UserProcessor`.
 *  2. Define [Resource] for `DataSource`, that also logs the [ExitCase].
 *  3. Compose `UserProcessor` and `DataSource` [Resource] together into a [Resource] for `Service`.
 *
 * <!--- INCLUDE
 * import arrow.fx.coroutines.Resource
 * import arrow.fx.coroutines.resource
 * import arrow.fx.coroutines.resourceScope
 * import kotlinx.coroutines.Dispatchers
 * import kotlinx.coroutines.withContext
 *
 * class UserProcessor {
 *   suspend fun start(): Unit = withContext(Dispatchers.IO) { println("Creating UserProcessor") }
 *   suspend fun shutdown(): Unit = withContext(Dispatchers.IO) {
 *     println("Shutting down UserProcessor")
 *   }
 * }
 *
 * class DataSource {
 *   fun connect(): Unit = println("Connecting dataSource")
 *   fun close(): Unit = println("Closed dataSource")
 * }
 *
 * class Service(val db: DataSource, val userProcessor: UserProcessor) {
 *   suspend fun processData(): List<String> = throw RuntimeException("I'm going to leak resources by not closing them")
 * }
 * -->
 * ```kotlin
 * val userProcessor: Resource<UserProcessor> = resource({
 *   UserProcessor().also { it.start() }
 * }) { p, _ -> p.shutdown() }
 *
 * val dataSource: Resource<DataSource> = resource({
 *   DataSource().also { it.connect() }
 * }) { ds, exitCase ->
 *   println("Releasing $ds with exit: $exitCase")
 *   withContext(Dispatchers.IO) { ds.close() }
 * }
 *
 * val service: Resource<Service> = resource {
 *   Service(dataSource.bind(), userProcessor.bind())
 * }
 *
 * suspend fun main(): Unit = resourceScope {
 *   val data = service.bind().processData()
 *   println(data)
 * }
 * ```
 * <!--- KNIT example-resource-03.kt -->
 *
 * There is a lot going on in the snippet above, which we'll analyse in the sections below.
 * Looking at the above example it should already give you some idea if the capabilities of [Resource].
 *
 * ## Resource constructors
 *
 * [Resource] works entirely through a DSL,
 * which allows _installing_ a `Resource` through the `suspend fun <A> install(acquire: suspend () -> A, release: suspend (A, ExitCase) -> Unit): A` function.
 *
 * `acquire` is used to _allocate_ the `Resource`,
 * and before returning the resource `A` it also install the `release` handler into the `ResourceScope`.
 *
 * We can use `suspend fun` with `Scope` as an extension function receiver to create synthetic constructors for our `Resource`s.
 * If you're using _context receivers_ you can also use `context(Scope)` instead.
 *
 * <!--- INCLUDE
 * import arrow.fx.coroutines.ResourceScope
 * import arrow.fx.coroutines.Resource
 * import arrow.fx.coroutines.resource
 * import kotlinx.coroutines.Dispatchers
 * import kotlinx.coroutines.withContext
 *
 * class UserProcessor {
 *   suspend fun start(): Unit = withContext(Dispatchers.IO) { println("Creating UserProcessor") }
 *   suspend fun shutdown(): Unit = withContext(Dispatchers.IO) {
 *     println("Shutting down UserProcessor")
 *   }
 * }
 * -->
 * ```kotlin
 * suspend fun ResourceScope.userProcessor(): UserProcessor =
 *   install({  UserProcessor().also { it.start() } }) { processor, _ ->
 *     processor.shutdown()
 *   }
 * ```
 *
 * We can of course also create `lazy` representations of this by wrapping `install` in [resource] and returning the `suspend lambda` value instead.
 *
 * ```kotlin
 * val userProcessor: Resource<UserProcessor> = resource {
 *   val x: UserProcessor = install(
 *     {  UserProcessor().also { it.start() } },
 *     { processor, _ -> processor.shutdown() }
 *   )
 *   x
 * }
 * ```
 *
 * There is also a convenience operator for this pattern, but you might have preferred `ResourceScope::userProcessor` instead since it yields the same result.
 *
 * ```kotlin
 * val userProcessor2: Resource<UserProcessor> = resource({
 *   UserProcessor().also { it.start() }
 * }) { processor, _ -> processor.shutdown() }
 *
 * val userProcessor3: Resource<UserProcessor> = ResourceScope::userProcessor
 * ```
 * <!--- KNIT example-resource-04.kt -->
 *
 * ## Scope DSL
 *
 * The [ResourceScope] DSL allows you to _install_ resources, and interact with them in a safe way.
 *
 * Arrow offers the same elegant `bind` DSL for Resource composition as you might be familiar with from Arrow Core. Which we've already seen above, in our first example.
 * What is more interesting, is that we can also compose it with any other existing pattern from Arrow!
 * Let's compose our `UserProcessor` and `DataSource` in parallel, so that their `start` and `connect` methods can run in parallel.
 *
 * <!--- INCLUDE
 * import arrow.fx.coroutines.ResourceScope
 * import arrow.fx.coroutines.resourceScope
 * import arrow.fx.coroutines.parZip
 * import kotlinx.coroutines.Dispatchers
 * import kotlinx.coroutines.withContext
 *
 * class UserProcessor {
 *   suspend fun start(): Unit = withContext(Dispatchers.IO) { println("Creating UserProcessor") }
 *   suspend fun shutdown(): Unit = withContext(Dispatchers.IO) {
 *     println("Shutting down UserProcessor")
 *   }
 * }
 *
 * class DataSource {
 *   suspend fun connect(): Unit = withContext(Dispatchers.IO) { println("Connecting dataSource") }
 *   suspend fun close(): Unit = withContext(Dispatchers.IO) { println("Closed dataSource") }
 * }
 *
 * class Service(val db: DataSource, val userProcessor: UserProcessor) {
 *   suspend fun processData(): List<String> = (0..10).map { "Processed : $it" }
 * }
 * -->
 * ```kotlin
 * suspend fun ResourceScope.userProcessor(): UserProcessor =
 *   install({ UserProcessor().also { it.start() } }){ p,_ -> p.shutdown() }
 *
 * suspend fun ResourceScope.dataSource(): DataSource =
 *   install({ DataSource().also { it.connect() } }) { ds, _ -> ds.close() }
 *
 * suspend fun main(): Unit = resourceScope {
 *   val service = parZip({ userProcessor() }, { dataSource() }) { userProcessor, ds ->
 *     Service(ds, userProcessor)
 *   }
 *   val data = service.processData()
 *   println(data)
 * }
 * ```
 * <!--- KNIT example-resource-05.kt -->
 *
 * ## Conclusion
 *
 * [Resource] guarantee that their release finalizers are always invoked in the correct order when an exception is raised or the [kotlinx.coroutines.Job] is running gets canceled.
 *
 * To achieve this [Resource] ensures that the `acquire` & `release` step are [NonCancellable].
 * If a cancellation signal, or an exception is received during `acquire`, the resource is assumed to not have been acquired and thus will not trigger the release function.
 *  => Any composed resources that are already acquired they will be guaranteed to release as expected.
 *
 * If you don't need a data-type like [Resource] but want a function alternative to `try/catch/finally` with automatic error composition,
 * and automatic [NonCancellable] `acquire` and `release` steps use [bracketCase] or [bracket].
 **/
public typealias Resource<A> = suspend ResourceScope.() -> A

/**
 * This Marker exists to prevent being able to call `bind` from `install`, and its derived methods.
 * This is done to ensure correct usage of [ResourceScope].
 */
@ResourceDSL
public object AcquireStep

@ResourceDSL
public interface ResourceScope : AutoCloseScope {

  /**
   * Compose another [Resource] program into this [ResourceScope].
   * All [release] functions [install]ed into the [Resource] lambda will be installed in this [ResourceScope] while respecting the FIFO order.
   */
  @ResourceDSL
  public suspend fun <A> Resource<A>.bind(): A = this()

  /**
   * Install [A] into the [ResourceScope].
   * Its [release] function will be called with the appropriate [ExitCase] if this [ResourceScope] finishes.
   * It results either in [ExitCase.Completed], [ExitCase.Cancelled] or [ExitCase.Failure] depending on the terminal state of [Resource] lambda.
   */
  @ResourceDSL
  public suspend fun <A> install(
    acquire: suspend AcquireStep.() -> A,
    release: suspend (A, ExitCase) -> Unit,
  ): A = withContext(NonCancellable) {
    acquire(AcquireStep).also { a -> onRelease { release(a, it) } }
  }

  /** Composes a [release] action to a [Resource] value before binding. */
  @ResourceDSL
  public suspend infix fun <A> Resource<A>.release(release: suspend (A) -> Unit): A =
    bind().also { a -> onRelease { release(a) } }

  /** Composes a [releaseCase] action to a [Resource] value before binding. */
  @ResourceDSL
  public suspend infix fun <A> Resource<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): A =
    bind().also { a -> onRelease { release(a, it) } }

  override fun onClose(release: (Throwable?) -> Unit): Unit = onRelease { release(it.errorOrNull) }

  public infix fun onRelease(release: suspend (ExitCase) -> Unit)
}

@ScopeDSL
public fun <A> resource(block: suspend ResourceScope.() -> A): Resource<A> = block

/**
 * The [resourceScope] function creates the [ResourceScope] instances,
 * runs the user [action] while allocating the _installed_ resources.
 * upon [ExitCase.Completed], [ExitCase.Cancelled] or [ExitCase.Failure] runs all the `release` finalizers.
 *
 * <!--- INCLUDE
 * import arrow.fx.coroutines.resourceScope
 * import kotlinx.coroutines.Dispatchers
 * import kotlinx.coroutines.withContext
 *
 * class DataSource {
 *   suspend fun connect(): Unit = withContext(Dispatchers.IO) { println("Connecting dataSource") }
 *   suspend fun close(): Unit = withContext(Dispatchers.IO) { println("Closed dataSource") }
 *   suspend fun users(): List<String> = listOf("User-1", "User-2", "User-3")
 * }
 * -->
 * ```kotlin
 * suspend fun main(): Unit = resourceScope {
 *   val dataSource = install({
 *     DataSource().also { it.connect() }
 *   }) { ds, _ -> ds.close() }
 *
 *   println("Using data source: ${dataSource.users()}")
 * }
 * ```
 * <!--- KNIT example-resource-06.kt -->
 */
@ScopeDSL
@OptIn(DelicateCoroutinesApi::class)
@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE")
public suspend inline fun <A> resourceScope(action: suspend ResourceScope.() -> A): A {
  contract {
    callsInPlace(action, InvocationKind.EXACTLY_ONCE)
  }
  val (scope, cancelAll) = resource { this }.allocate()
  return finalizeCase({ scope.action() }) { cancelAll(it) }
}

@Suppress("REDUNDANT_INLINE_SUSPEND_FUNCTION_TYPE")
public suspend inline infix fun <A, B> Resource<A>.use(f: suspend (A) -> B): B {
  contract {
    callsInPlace(f, InvocationKind.EXACTLY_ONCE)
  }
  return resourceScope { f(bind()) }
}

/**
 * Construct a [Resource] from an allocating function [acquire] and a release function [release].
 *
 * ```kotlin
 * import arrow.fx.coroutines.resource
 * import arrow.fx.coroutines.resourceScope
 *
 * val resource = resource {
 *   install({ 42.also { println("Getting expensive resource") } }) { r, exitCase ->
 *     println("Releasing expensive resource: $r, exit: $exitCase")
 *   }
 * }
 *
 * suspend fun main(): Unit = resourceScope {
 *   val res = resource.bind()
 *   println("Expensive resource under use! $res")
 * }
 * ```
 * <!--- KNIT example-resource-07.kt -->
 */
public fun <A> resource(
  acquire: suspend () -> A,
  release: suspend (A, ExitCase) -> Unit,
): Resource<A> = resource {
  install({ acquire() }, release)
}

/**
 * Runs [Resource].[use] and emits [A] of the resource
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import kotlinx.coroutines.*
 * import kotlinx.coroutines.flow.*
 * import java.nio.file.Path
 * import kotlin.io.path.*
 *
 * @OptIn(ExperimentalCoroutinesApi::class)
 * fun Flow<ByteArray>.writeAll(path: Path): Flow<Unit> =
 *   closeable { path.toFile().outputStream() }
 *     .asFlow()
 *     .flatMapConcat { writer -> map { writer.write(it) } }
 *     .flowOn(Dispatchers.IO)
 *
 * fun Path.readAll(): Flow<String> = flow {
 *   useLines { lines -> emitAll(lines.asFlow()) }
 * }
 *
 * suspend fun main() {
 *   Path("example.kt")
 *     .readAll()
 *     .collect(::println)
 * }
 * ```
 * <!--- KNIT example-resource-08.kt -->
 */
public fun <A> Resource<A>.asFlow(): Flow<A> =
  flow {
    resourceScope {
      emit(bind())
    }
  }

/**
 * Deconstruct [Resource] into an [A] and a `release` handler.
 * The `release` action **must** always be called, if  never called, then the resource [A] will leak.
 * The `release` step is already made `NonCancellable` to guarantee correct invocation like `Resource` or `bracketCase`,
 * and it will automatically rethrow, and compose, the exceptions as needed.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import arrow.fx.coroutines.ExitCase.Companion.ExitCase
 *
 * val resource =
 *   resource({ "Acquire" }) { _, exitCase -> println("Release $exitCase") }
 *
 * @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
 * suspend fun main(): Unit {
 *   val (acquired: String, release: suspend (ExitCase) -> Unit) = resource.allocate()
 *   try {
 *     /** Do something with A */
 *     release(ExitCase.Completed)
 *   } catch(e: Throwable) {
 *      release(ExitCase(e))
 *   }
 * }
 * ```
 * <!--- KNIT example-resource-09.kt -->
 *
 * This is a **delicate** API. It is easy to accidentally create resource or memory leaks [allocate] is used.
 * A [Resource] allocated by [allocate] is not subject to the guarantees that [Resource] makes,
 * instead the caller is responsible for correctly invoking the `release` handler at the appropriate time.
 * This API is useful for building inter-op APIs between [Resource] and non-suspending code, such as Java libraries.
 */
@DelicateCoroutinesApi
public suspend fun <A> Resource<A>.allocate(): Pair<A, suspend (ExitCase) -> Unit> = with(ResourceScopeImpl()) {
  bind() to this::cancelAll
}

internal class ResourceScopeImpl : ResourceScope {
  private val finalizers: Atomic<List<suspend (ExitCase) -> Unit>> = Atomic(emptyList())
  override fun onRelease(release: suspend (ExitCase) -> Unit) {
    finalizers.update(release::prependTo)
  }

  suspend fun cancelAll(exitCase: ExitCase) {
    withContext(NonCancellable) {
      finalizers.getAndSet(emptyList()).fold(exitCase.errorOrNull) { acc, finalizer ->
        acc.add(runCatching { finalizer(exitCase) }.exceptionOrNull())
      }
    }?.let { throw it }
  }

  private fun Throwable?.add(other: Throwable?): Throwable? {
    if (other !is CancellationException) other?.nonFatalOrThrow()
    return this?.apply {
      other?.let { addSuppressed(it) }
    } ?: other
  }
}

/** Platform-dependent IO [CoroutineDispatcher] **/
internal expect val IODispatcher: CoroutineDispatcher

/**
 * Creates a [Resource] from an [AutoCloseable], which uses [AutoCloseable.close] for releasing.
 *
 * ```kotlin
 * import arrow.fx.coroutines.resourceScope
 * import arrow.fx.coroutines.autoCloseable
 * import java.io.FileInputStream
 *
 * suspend fun copyFile(src: String, dest: String): Unit =
 *   resourceScope {
 *     val a: FileInputStream = autoCloseable { FileInputStream(src) }
 *     val b: FileInputStream = autoCloseable { FileInputStream(dest) }
 *     /** read from [a] and write to [b]. **/
 *   } // Both resources will be closed accordingly to their #close methods
 * ```
 * <!--- KNIT example-resource-10.kt -->
 */
@ResourceDSL
@OptIn(ExperimentalStdlibApi::class)  // 'AutoCloseable' in stdlib < 2.0
public suspend fun <A : AutoCloseable> ResourceScope.autoCloseable(
  closingDispatcher: CoroutineDispatcher = IODispatcher,
  autoCloseable: suspend () -> A,
): A {
  contract {
    callsInPlace(autoCloseable, InvocationKind.EXACTLY_ONCE)
  }
  // This is install({ autoCloseable() } ) { s: A, _ -> withContext(closingDispatcher) { s.close() } }
  // but inlined because `install` can't have a contract (since it's a member)
  return withContext(NonCancellable) {
    val s = autoCloseable()
    onRelease { withContext(closingDispatcher) { s.close() } }
    s
  }
}

@OptIn(ExperimentalStdlibApi::class)  // 'AutoCloseable' in stdlib < 2.0
public fun <A : AutoCloseable> autoCloseable(
  closingDispatcher: CoroutineDispatcher = IODispatcher,
  autoCloseable: suspend () -> A,
): Resource<A> = resource {
  autoCloseable(closingDispatcher, autoCloseable)
}
