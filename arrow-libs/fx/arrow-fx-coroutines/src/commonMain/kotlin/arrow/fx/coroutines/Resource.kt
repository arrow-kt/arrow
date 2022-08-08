package arrow.fx.coroutines

import arrow.core.continuations.AtomicRef
import arrow.core.continuations.update
import arrow.core.identity
import arrow.core.prependTo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.jvm.JvmInline

@DslMarker
public annotation class ResourceDSL

/**
 * [Resource] models resource allocation and releasing.
 * It is especially useful when multiple resources that depend on each other need to be acquired and later released in reverse order,
 * or when you want to load independent resources in parallel.
 *
 * When a resource is created we can call [use] to run a suspending computation with the resource.
 * The finalizers are then guaranteed to run afterwards in reverse order of acquisition.
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
 *   1. Acquiring the resource of [A].
 *   2. Using [A].
 *   3. Releasing [A] with [ExitCase.Completed], [ExitCase.Failure] or [ExitCase.Cancelled].
 *
 * We rewrite our previous example to [Resource] below by:
 *  1. Define [Resource] for `UserProcessor`.
 *  2. Define [Resource] for `DataSource`, that also logs the [ExitCase].
 *  3. Compose `UserProcessor` and `DataSource` [Resource] together into a [Resource] for `Service`.
 *
 * <!--- INCLUDE
 * import arrow.fx.coroutines.resource
 * import arrow.fx.coroutines.release
 * import arrow.fx.coroutines.releaseCase
 * import arrow.fx.coroutines.use
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
 * val userProcessor = resource {
 *   UserProcessor().also {
 *     it.start()
 *   }
 * } release UserProcessor::shutdown
 *
 * val dataSource = resource {
 *   DataSource().also { it.connect() }
 * } releaseCase { ds, exitCase ->
 *   println("Releasing $ds with exit: $exitCase")
 *   withContext(Dispatchers.IO) { ds.close() }
 * }
 *
 * val service = resource {
 *   Service(dataSource.bind(), userProcessor.bind())
 * }
 *
 * suspend fun main(): Unit {
 *   service.use { it.processData() }
 * }
 * ```
 * <!--- KNIT example-resource-03.kt -->
 *
 * There is a lot going on in the snippet above, which we'll analyse in the sections below.
 * Looking at the above example it should already give you some idea if the capabilities of [Resource].
 *
 * ## Resource constructors
 *
 * [Resource] only has a single constructors, and its DSL.
 * The [Resource] constructor takes `acquire: suspend () -> A` and `releaseCase: suspend (A, ExitCase) -> Unit`, which we see used in the example above.
 *
 * <!--- INCLUDE
 * import arrow.fx.coroutines.resource
 * import arrow.fx.coroutines.Resource
 * import arrow.fx.coroutines.release
 * import arrow.fx.coroutines.releaseCase
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
 * val userProcessor: Resource<UserProcessor> =
 *   resource(
 *     {  UserProcessor().also { it.start() } },
 *     { processor, _ -> processor.shutdown() }
 *   )
 * ```
 *
 * We can also create a [Resource] using the [resource] DSL, and then attaching a [release] or [releaseCase] function.
 *
 * ```kotlin
 * val userProcessor2: Resource<UserProcessor> = resource {
 *   UserProcessor().also { it.start() }
 * } release UserProcessor::shutdown
 *
 * val userProcessor3 = userProcessor2 releaseCase { _, exitCase ->
 *   println("Composed finalizer to log exitCase: $exitCase")
 * }
 * ```
 * <!--- KNIT example-resource-04.kt -->
 *
 * ## Composing Resource / Resource DSL
 *
 * Arrow offers the same elegant `bind` DSL for Resource composition as you might be familiar with from Arrow Core. Which we've already seen above, in our first example.
 * What is more interesting, is that we can also compose it with any other existing pattern from Arrow!
 * Let's compose our `UserProcessor` and `DataSource` in parallel, so that their `start` and `connect` methods can run in parallel.
 *
 * <!--- INCLUDE
 * import arrow.fx.coroutines.resource
 * import arrow.fx.coroutines.release
 * import arrow.fx.coroutines.parZip
 * import arrow.fx.coroutines.use
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
 *   suspend fun processData(): List<String> = throw RuntimeException("I'm going to leak resources by not closing them")
 * }
 * -->
 * ```kotlin
 * val userProcessor = resource {
 *   UserProcessor().also { it.start() }
 * } release UserProcessor::shutdown
 *
 * val dataSource = resource {
 *   DataSource().also { it.connect() }
 * } release DataSource::close
 *
 * val service = resource {
 *   parZip({ userProcessor.bind() }, { dataSource.bind() }) { userProcessor, ds ->
 *     Service(ds, userProcessor)
 *   }
 * }
 *
 * suspend fun main(): Unit {
 *   service.use(Service::processData)
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

public interface ResourceScope {
  public suspend fun <A> Resource<A>.bind(): A
  
  @ResourceDSL
  public suspend fun <A> resource(
    acquire: suspend () -> A,
    release: suspend (A, ExitCase) -> Unit,
  ): A
  
  /**
   * Composes a [release] action to a [Resource.use] action creating a [Resource].
   */
  @ResourceDSL
  public suspend infix fun <A> Resource<A>.release(release: suspend (A) -> Unit): A =
    resource({ bind() }) { a, _ -> release(a) }
  
  /**
   * Composes a [releaseCase] action to a [Resource.use] action creating a [Resource].
   */
  @ResourceDSL
  public suspend infix fun <A> Resource<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): A =
    resource({ bind() }, release)
}

public fun <A> resource(block: suspend ResourceScope.() -> A): Resource<A> = block


/**
 * [use] the created resource,
 * upon [ExitCase.Completed], [ExitCase.Cancelled] or [ExitCase.Failure] run all [release] finalizers.
 *
 * ```kotlin
 * import arrow.fx.coroutines.resource
 * import arrow.fx.coroutines.release
 * import arrow.fx.coroutines.use
 * import kotlinx.coroutines.Dispatchers
 * import kotlinx.coroutines.withContext
 *
 * class DataSource {
 *   suspend fun connect(): Unit = withContext(Dispatchers.IO) { println("Connecting dataSource") }
 *   suspend fun close(): Unit = withContext(Dispatchers.IO) { println("Closed dataSource") }
 *   suspend fun users(): List<String> = listOf("User-1", "User-2", "User-3")
 * }
 *
 * suspend fun main(): Unit {
 *   val dataSource = resource {
 *     DataSource().also { it.connect() }
 *   } release DataSource::close
 *
 *   val res = dataSource
 *     .use { ds -> "Using data source: ${ds.users()}" }
 *     .also(::println)
 * }
 * ```
 * <!--- KNIT example-resource-06.kt -->
 */
public suspend infix fun <A, B> Resource<A>.use(f: suspend (A) -> B): B {
  val effect = ResourceScopeImpl()
  val b = try {
    val a = invoke(effect)
    f(a)
  } catch (e: Throwable) {
    val ex = if (e is CancellationException) ExitCase.Cancelled(e) else ExitCase.Failure(e)
    val ee = withContext(NonCancellable) {
      effect.cancelAll(ex, e) ?: e
    }
    throw ee
  }
  withContext(NonCancellable) {
    effect.cancelAll(ExitCase.Completed)?.let { throw it }
  }
  return b
}

/**
 * Construct a [Resource] from an allocating function [acquire] and a release function [release].
 *
 * ```kotlin
 * import arrow.fx.coroutines.resource
 * import arrow.fx.coroutines.use
 *
 * val resource = resource(
 *   { 42.also { println("Getting expensive resource") } },
 *   { r, exitCase -> println("Releasing expensive resource: $r, exit: $exitCase") }
 * )
 *
 * suspend fun main(): Unit =
 *   resource.use { println("Expensive resource under use! $it") }
 * ```
 * <!--- KNIT example-resource-07.kt -->
 */
public fun <A> resource(
  acquire: suspend () -> A,
  release: suspend (A, ExitCase) -> Unit,
): Resource<A> = resource { resource(acquire, release) }

/**
 * Composes a [release] action to a [Resource.use] action creating a [Resource].
 */
public infix fun <A> Resource<A>.release(release: suspend (A) -> Unit): Resource<A> =
  resource {
    resource({ bind() }) { a, _ -> release(a) }
  }

/**
 * Composes a [releaseCase] action to a [Resource.use] action creating a [Resource].
 */
public infix fun <A> Resource<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): Resource<A> =
  resource {
    resource({ bind() }, release)
  }

/**
 * Runs [use] and emits [A] of the resource
 *
 * ```kotlin
 * import arrow.fx.coroutines.asFlow
 * import arrow.fx.coroutines.closeable
 * import kotlinx.coroutines.Dispatchers
 * import kotlinx.coroutines.flow.flow
 * import kotlinx.coroutines.flow.Flow
 * import kotlinx.coroutines.flow.asFlow
 * import kotlinx.coroutines.flow.emitAll
 * import kotlinx.coroutines.flow.flatMapConcat
 * import kotlinx.coroutines.flow.flowOn
 * import kotlinx.coroutines.flow.map
 * import java.nio.file.Path
 * import kotlin.io.path.useLines
 *
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
 *   Path.of("example.kt")
 *     .readAll()
 *     .collect(::println)
 * }
 * ```
 * <!--- KNIT example-resource-08.kt -->
 */
public fun <A> Resource<A>.asFlow(): Flow<A> =
  flow {
    use {
      emit(it)
    }
  }

@JvmInline
private value class ResourceScopeImpl(
  private val finalizers: AtomicRef<List<suspend (ExitCase) -> Unit>> = AtomicRef(emptyList()),
) : ResourceScope {
  override suspend fun <A> Resource<A>.bind(): A = invoke(this@ResourceScopeImpl)
  
  override suspend fun <A> resource(acquire: suspend () -> A, release: suspend (A, ExitCase) -> Unit): A =
    bracketCase({
      val a = acquire()
      val finalizer: suspend (ExitCase) -> Unit = { ex: ExitCase -> release(a, ex) }
      finalizers.update(finalizer::prependTo)
      a
    }, ::identity, { a, ex ->
      // Only if ExitCase.Failure, or ExitCase.Cancelled during acquire we cancel
      // Otherwise we've saved the finalizer, and it will be called from somewhere else.
      if (ex != ExitCase.Completed) {
        val e = cancelAll(ex)
        val e2 = kotlin.runCatching { release(a, ex) }.exceptionOrNull()
        Platform.composeErrors(e, e2)?.let { throw it }
      }
    })
  
  suspend fun cancelAll(
    exitCase: ExitCase,
    first: Throwable? = null,
  ): Throwable? = finalizers.get().fold(first) { acc, finalizer ->
    val other = kotlin.runCatching { finalizer(exitCase) }.exceptionOrNull()
    other?.let {
      acc?.apply { addSuppressed(other) } ?: other
    } ?: acc
  }
}
