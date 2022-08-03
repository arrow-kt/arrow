package arrow.fx.coroutines

import arrow.core.continuations.AtomicRef
import arrow.core.continuations.update
import arrow.core.identity
import arrow.core.prependTo
import arrow.fx.coroutines.continuations.ResourceScope
import arrow.fx.coroutines.continuations.resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * [Resource] models resource allocation and releasing. It is especially useful when multiple resources that depend on each other
 *  need to be acquired and later released in reverse order.
 * Or when you want to load independent resources in parallel.
 *
 * When a resource is created we can call [use] to run a suspend computation with the resource. The finalizers are then
 *  guaranteed to run afterwards in reverse order of acquisition.
 *
 * Consider the following use case:
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * class UserProcessor {
 *   fun start(): Unit = println("Creating UserProcessor")
 *   fun shutdown(): Unit = println("Shutting down UserProcessor")
 *   fun process(ds: DataSource): List<String> =
 *    ds.users().map { "Processed $it" }
 * }
 *
 * class DataSource {
 *   fun connect(): Unit = println("Connecting dataSource")
 *   fun users(): List<String> = listOf("User-1", "User-2", "User-3")
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
 * In the following example, we are creating and using a service that has a dependency on two resources: A database and a processor. All resources need to be closed in the correct order at the end.
 * However, this program is not safe because it is prone to leak `dataSource` and `userProcessor` when an exception or cancellation signal occurs whilst using the service.
 * As a consequence of the resource leak, this program does not guarantee the correct release of resources if something fails while acquiring or using the resource. Additionally manually keeping track of acquisition effects is an unnecessary overhead.
 *
 * We can split the above program into 3 different steps:
 *   1. Acquiring the resource
 *   2. Using the resource
 *   3. Releasing the resource with either [ExitCase.Completed], [ExitCase.Failure] or [ExitCase.Cancelled].
 *
 * That is exactly what `Resource` does, and how we can solve our problem:
 *
 * # Constructing Resource
 *
 * Creating a resource can be easily done by the `resource` DSL,
 * and there are two ways to define the finalizers with `release` or `releaseCase`.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * val resourceA = resource {
 *   "A"
 * } release { a ->
 *   println("Releasing $a")
 * }
 *
 * val resourceB = resource {
 *  "B"
 * } releaseCase { b, exitCase ->
 *   println("Releasing $b with exit: $exitCase")
 * }
 * ```
 * <!--- KNIT example-resource-02.kt -->
 *
 * Here `releaseCase` also signals with what [ExitCase] state the `use` step finished.
 *
 * # Using and composing Resource
 *
 * Arrow offers the same elegant `bind` DSL for Resource as you might be familiar with from Arrow Core.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 * import arrow.fx.coroutines.continuations.resource
 *
 * class UserProcessor {
 *   fun start(): Unit = println("Creating UserProcessor")
 *   fun shutdown(): Unit = println("Shutting down UserProcessor")
 *   fun process(ds: DataSource): List<String> =
 *    ds.users().map { "Processed $it" }
 * }
 *
 * class DataSource {
 *   fun connect(): Unit = println("Connecting dataSource")
 *   fun users(): List<String> = listOf("User-1", "User-2", "User-3")
 *   fun close(): Unit = println("Closed dataSource")
 * }
 *
 * class Service(val db: DataSource, val userProcessor: UserProcessor) {
 *   suspend fun processData(): List<String> = userProcessor.process(db)
 * }
 *
 * val userProcessor = resource {
 *   UserProcessor().also(UserProcessor::start)
 * } release UserProcessor::shutdown
 *
 * val dataSource = resource {
 *   DataSource().also { it.connect() }
 * } release DataSource::close
 *
 * suspend fun main(): Unit {
 *   resource {
 *     parZip({ userProcessor.bind() }, { dataSource.bind() }) { userProcessor, ds ->
 *       Service(ds, userProcessor)
 *     }
 *   }.use { service -> service.processData() }
 * }
 * ```
 * <!--- KNIT example-resource-03.kt -->
 *
 * [Resource]s are immutable and can be composed using [zip] or [parZip].
 * [Resource]s guarantee that their release finalizers are always invoked in the correct order when an exception is raised or the context where the program is running gets canceled.
 *
 * To achieve this [Resource] ensures that the `acquire` & `release` step are [NonCancellable].
 * If a cancellation signal, or an exception is received during `acquire`, the resource is assumed to not have been acquired and thus will not trigger the release function.
 *  => Any composed resources that are already acquired they will be guaranteed to release as expected.
 *
 * If you don't need a data-type like [Resource] but want a function alternative to `try/catch/finally` with automatic error composition,
 * and automatic [NonCancellable] `acquire` and `release` steps use [bracketCase] or [bracket].
 **/
public sealed class Resource<out A> {
  
  /**
   * Use the created resource
   * When done will run all finalizers
   *
   * ```kotlin
   * import arrow.fx.coroutines.*
   *
   * class DataSource {
   *   fun connect(): Unit = println("Connecting dataSource")
   *   fun users(): List<String> = listOf("User-1", "User-2", "User-3")
   *   fun close(): Unit = println("Closed dataSource")
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
   * <!--- KNIT example-resource-04.kt -->
   */
  public suspend infix fun <B> use(f: suspend (A) -> B): B =
    when (this) {
      is Allocate -> bracketCase(acquire, f, release)
      is Dsl -> {
        val effect = ResourceScopeImpl()
        val b = try {
          val a = dsl(effect)
          f(a)
        } catch (e: Throwable) {
          val ex = if (e is CancellationException) ExitCase.Cancelled(e) else ExitCase.Failure(e)
          val ee = withContext(NonCancellable) {
            effect.finalizers.get().cancelAll(ex, e) ?: e
          }
          throw ee
        }
        withContext(NonCancellable) {
          effect.finalizers.get().cancelAll(ExitCase.Completed)?.let { throw it }
        }
        b
      }
    }
  
  public fun <B> map(f: suspend (A) -> B): Resource<B> =
    resource { f(bind()) }
  
  /** Useful for setting up/configuring an acquired resource */
  public fun tap(f: suspend (A) -> Unit): Resource<A> =
    resource { bind().also { f(it) } }
  
  /**
   * Chain two [Resource] in sequence.
   * Shorthand for `resource { f(bind()).bind() }`
   */
  public fun <B> flatMap(f: (A) -> Resource<B>): Resource<B> =
    resource { f(bind()).bind() }
  
  public class Allocate<A>(
    public val acquire: suspend () -> A,
    public val release: suspend (A, ExitCase) -> Unit,
  ) : Resource<A>()
  
  public data class Dsl<A>(public val dsl: suspend ResourceScope.() -> A) : Resource<A>()
  
  public companion object
}

/**
 * Construct a [Resource] from an allocating function [acquire] and a release function [release].
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * suspend fun acquireResource(): Int = 42.also { println("Getting expensive resource") }
 * suspend fun releaseResource(r: Int, exitCase: ExitCase): Unit = println("Releasing expensive resource: $r, exit: $exitCase")
 *
 * suspend fun main(): Unit {
 *   val resource = Resource(::acquireResource, ::releaseResource)
 *   resource.use {
 *     println("Expensive resource under use! $it")
 *   }
 * }
 * ```
 * <!--- KNIT example-resource-08.kt -->
 */
public fun <A> Resource(
  acquire: suspend () -> A,
  release: suspend (A, ExitCase) -> Unit,
): Resource<A> = Resource.Allocate(acquire, release)

/**
 * Composes a [release] action to a [Resource.use] action creating a [Resource].
 */
public infix fun <A> Resource<A>.release(release: suspend (A) -> Unit): Resource<A> =
  resource {
    val a = bind()
    Resource({ a }, { _, _ -> release(a) }).bind()
  }

/**
 * Composes a [releaseCase] action to a [Resource.use] action creating a [Resource].
 */
public infix fun <A> Resource<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): Resource<A> =
  resource {
    val a = bind()
    Resource({ a }, { _, ex -> release(a, ex) }).bind()
  }

/**
 * Runs [Resource.use] and emits [A] of the resource
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * fun Flow<ByteArray>.writeAll(path: Path): Flow<Unit> =
 *   Resource.fromCloseable { path.toFile().outputStream() }
 *     .asFlow()
 *     .flatMapConcat { writer -> byteFlow.map { writer.write(it) } }
 *     .flowOn(Dispatchers.IO)
 *
 * fun Path.readAll(): Flow<String> = flow {
 *   path.useLines { lines -> emitAll(lines) }
 * }
 *
 * Path("example.kt")
 *   .readAll()
 *   .
 * ```
 */
public fun <A> Resource<A>.asFlow(): Flow<A> =
  flow {
    use {
      emit(it)
    }
  }

private class ResourceScopeImpl : ResourceScope {
  val finalizers: AtomicRef<List<suspend (ExitCase) -> Unit>> = AtomicRef(emptyList())
  override suspend fun <A> Resource<A>.bind(): A =
    when (this) {
      is Resource.Dsl -> dsl.invoke(this@ResourceScopeImpl)
      is Resource.Allocate -> bracketCase({
        val a = acquire()
        val finalizer: suspend (ExitCase) -> Unit = { ex: ExitCase -> release(a, ex) }
        finalizers.update(finalizer::prependTo)
        a
      }, ::identity, { a, ex ->
        // Only if ExitCase.Failure, or ExitCase.Cancelled during acquire we cancel
        // Otherwise we've saved the finalizer, and it will be called from somewhere else.
        if (ex != ExitCase.Completed) {
          val e = finalizers.get().cancelAll(ex)
          val e2 = runCatching { release(a, ex) }.exceptionOrNull()
          Platform.composeErrors(e, e2)?.let { throw it }
        }
      })
    }
}

private suspend fun List<suspend (ExitCase) -> Unit>.cancelAll(
  exitCase: ExitCase,
  first: Throwable? = null,
): Throwable? = fold(first) { acc, finalizer ->
  val other = kotlin.runCatching { finalizer(exitCase) }.exceptionOrNull()
  other?.let {
    acc?.apply { addSuppressed(other) } ?: other
  } ?: acc
}
