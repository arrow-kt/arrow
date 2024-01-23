package arrow.fx.coroutines

import arrow.atomic.update
import arrow.atomic.Atomic
import arrow.core.identity
import arrow.core.prependTo
import arrow.core.zip
import arrow.fx.coroutines.ExitCase.Cancelled
import arrow.fx.coroutines.ExitCase.Companion.ExitCase
import arrow.fx.coroutines.ExitCase.Completed
import arrow.fx.coroutines.ExitCase.Failure
import arrow.fx.coroutines.continuations.AcquireStep
import arrow.fx.coroutines.continuations.ScopeDSL
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalTypeInference

public typealias ResourceScope = arrow.fx.coroutines.continuations.ResourceScope

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
   * <!--- KNIT example-resource-06.kt -->
   */
  @Suppress("UNCHECKED_CAST")
  public tailrec suspend infix fun <B> use(f: suspend (A) -> B): B =
    when (this) {
      is Dsl -> {
        val effect = ResourceScopeImpl()
        val b = try {
          val a = dsl(effect)
          f(a)
        } catch (e: Throwable) {
          val ee = withContext(NonCancellable) {
            effect.finalizers.get().cancelAll(ExitCase(e), e) ?: e
          }
          throw ee
        }
        withContext(NonCancellable) {
          effect.finalizers.get().cancelAll(ExitCase.Completed)?.let { throw it }
        }
        b
      }

      is Allocate -> bracketCase(acquire, f, release)
      is Bind<*, *> -> Dsl {
        val any = source.bind()
        val ff = this@Resource.f as (Any?) -> Resource<A>
        ff(any).bind()
      }.use(f)

      is Defer -> resource().use(f)
    }

  @Deprecated(
    "map $nextVersionRemoved",
    ReplaceWith(
      "resource { f(bind()) }",
      "arrow.fx.coroutines.resource"
    )
  )
  public fun <B> map(f: suspend (A) -> B): Resource<B> =
    resource { f(bind()) }

  /** Useful for setting up/configuring an acquired resource */
  @Deprecated(
    "tap $nextVersionRemoved",
    ReplaceWith(
      "resource { bind().also { f(it) } }",
      "arrow.fx.coroutines.resource"
    )
  )
  public fun tap(f: suspend (A) -> Unit): Resource<A> =
    resource { bind().also { f(it) } }

  @Deprecated(
    "ap $nextVersionRemoved",
    ReplaceWith(
      "resource { bind().let { a ->  ff.bind().invoke(a) } }",
      "arrow.fx.coroutines.resource"
    )
  )
  public fun <B> ap(ff: Resource<(A) -> B>): Resource<B> =
    resource { bind().let { a -> ff.bind().invoke(a) } }

  /**
   * Create a resource value of [B] from a resource [A] by mapping [f].
   *
   * Useful when there is a need to create resources that depend on other resources,
   * for combining independent values [zip] provides nicer syntax without the need for callback nesting.
   *
   * ```kotlin
   * import arrow.fx.coroutines.*
   *
   * object Connection
   * class DataSource {
   *   fun connect(): Unit = println("Connecting dataSource")
   *   fun connection(): Connection = Connection
   *   fun close(): Unit = println("Closed dataSource")
   * }
   *
   * class Database(private val database: DataSource) {
   *   fun init(): Unit = println("Database initialising . . .")
   *   fun shutdown(): Unit = println("Database shutting down . . .")
   * }
   *
   * suspend fun main(): Unit {
   *   val dataSource = resource {
   *     DataSource().also { it.connect() }
   *   } release DataSource::close
   *
   *   fun database(ds: DataSource): Resource<Database> =
   *     resource {
   *       Database(ds).also(Database::init)
   *     } release Database::shutdown
   *
   *   dataSource.flatMap(::database)
   *     .use { println("Using database which uses dataSource") }
   * }
   * ```
   * <!--- KNIT example-resource-07.kt -->
   *
   * @see zip to combine independent resources together
   * @see parZip for combining independent resources in parallel
   */

  @Deprecated(
    "flatMap $nextVersionRemoved",
    ReplaceWith(
      "resource { f(this.bind()).bind() }",
      "arrow.fx.coroutines.resource"
    )
  )
  public fun <B> flatMap(f: (A) -> Resource<B>): Resource<B> =
    resource { f(bind()).bind() }

  @Deprecated(
    "zip $nextVersionRemoved",
    ReplaceWith(
      "resource { combine(this.bind(), other.bind()) }",
      "arrow.fx.coroutines.resource"
    )
  )
  public inline fun <B, C> zip(other: Resource<B>, crossinline combine: (A, B) -> C): Resource<C> =
    resource { combine(bind(), other.bind()) }

  @Deprecated(
    "zip $nextVersionRemoved",
    ReplaceWith(
      "resource { Pair(this.bind(), other.bind()) }",
      "arrow.fx.coroutines.resource"
    )
  )
  public fun <B> zip(other: Resource<B>): Resource<Pair<A, B>> =
    zip(other, ::Pair)

  /**
   * Combines two independent resource values with the provided [map] function,
   * returning the resulting immutable [Resource] value.
   * The finalizers run in order of left to right by using [flatMap] under the hood,
   * but [zip] provides a nicer syntax for combining values that don't depend on each-other.
   *
   * Useful to compose up to 9 independent resources,
   * see example for more details on how to use in code.
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
   *   userProcessor.zip(dataSource) { userProcessor, ds ->
   *       Service(ds, userProcessor)
   *     }.use { service -> service.processData() }
   * }
   * ```
   * <!--- KNIT example-resource-08.kt -->
   *
   * @see parZip if you want to combine independent resources in parallel
   * @see flatMap to combine resources that rely on each-other.
   */
  @Deprecated(
    "zip $nextVersionRemoved",
    ReplaceWith(
      "resource { map(this.bind(), b.bind(), c.bind()) }",
      "arrow.fx.coroutines.resource"
    )
  )
  public inline fun <B, C, D> zip(
    b: Resource<B>,
    c: Resource<C>,
    crossinline map: (A, B, C) -> D,
  ): Resource<D> =
    resource { map(bind(), b.bind(), c.bind()) }

  @Deprecated(
    "zip $nextVersionRemoved",
    ReplaceWith(
      "resource { map(this.bind(), b.bind(), c.bind(), d.bind()) }",
      "arrow.fx.coroutines.resource"
    )
  )
  public inline fun <B, C, D, E> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    crossinline map: (A, B, C, D) -> E,
  ): Resource<E> =
    resource { map(bind(), b.bind(), c.bind(), d.bind()) }

  @Deprecated(
    "zip $nextVersionRemoved",
    ReplaceWith(
      "resource { map(this.bind(), b.bind(), c.bind(), d.bind(), e.bind()) }",
      "arrow.fx.coroutines.resource"
    )
  )
  public inline fun <B, C, D, E, G> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    crossinline map: (A, B, C, D, E) -> G,
  ): Resource<G> =
    resource { map(bind(), b.bind(), c.bind(), d.bind(), e.bind()) }

  @Deprecated(
    "This method will be removed in Arrow 2.x.x in favor of the DSL",
    ReplaceWith(
      "zip $nextVersionRemoved",
      "arrow.fx.coroutines.resource"
    )
  )
  public inline fun <B, C, D, E, F, G, H> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    f: Resource<F>,
    crossinline map: (A, B, C, D, E, F) -> G,
  ): Resource<G> =
    resource { map(bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind()) }

  @Deprecated(
    "zip $nextVersionRemoved",
    ReplaceWith(
      "resource { map(this.bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind()) }",
      "arrow.fx.coroutines.resource"
    )
  )
  public inline fun <B, C, D, E, F, G, H> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    f: Resource<F>,
    g: Resource<G>,
    crossinline map: (A, B, C, D, E, F, G) -> H,
  ): Resource<H> =
    resource { map(bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind()) }

  @Deprecated(
    "zip $nextVersionRemoved",
    ReplaceWith(
      "resource { map(this.bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind()) }",
      "arrow.fx.coroutines.resource"
    )
  )
  public inline fun <B, C, D, E, F, G, H, I> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    f: Resource<F>,
    g: Resource<G>,
    h: Resource<H>,
    crossinline map: (A, B, C, D, E, F, G, H) -> I,
  ): Resource<I> =
    resource { map(bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind()) }

  @Deprecated(
    "zip $nextVersionRemoved",
    ReplaceWith(
      "resource { map(this.bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind()) }",
      "arrow.fx.coroutines.resource"
    )
  )
  public inline fun <B, C, D, E, F, G, H, I, J> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    f: Resource<F>,
    g: Resource<G>,
    h: Resource<H>,
    i: Resource<I>,
    crossinline map: (A, B, C, D, E, F, G, H, I) -> J,
  ): Resource<J> =
    resource { map(bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind()) }

  @Deprecated(
    "zip $nextVersionRemoved",
    ReplaceWith(
      "resource { map(this.bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind()) }",
      "arrow.fx.coroutines.resource"
    )
  )
  public inline fun <B, C, D, E, F, G, H, I, J, K> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    f: Resource<F>,
    g: Resource<G>,
    h: Resource<H>,
    i: Resource<I>,
    j: Resource<J>,
    crossinline map: (A, B, C, D, E, F, G, H, I, J) -> K,
  ): Resource<K> =
    resource { map(bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind()) }

  @Deprecated(
    "parZip $nextVersionRemoved",
    ReplaceWith(
      "resource { parZip<A, B, C>({ this.bind() }, { fb.bind() }) { a, b -> f(a, b) } }",
      "arrow.fx.coroutines.resource"
    )
  )
  public fun <B, C> parZip(fb: Resource<B>, f: suspend (A, B) -> C): Resource<C> =
    resource { parZip({ bind() }, { fb.bind() }) { a, b -> f(a, b) } }

  /**
   * Composes two [Resource]s together by zipping them in parallel,
   * by running both their `acquire` handlers in parallel, and both `release` handlers in parallel.
   *
   * Useful in the case that starting a resource takes considerable computing resources or time.
   *
   * ```kotlin
   * import arrow.fx.coroutines.*
   * import kotlinx.coroutines.delay
   *
   * class UserProcessor {
   *   suspend fun start(): Unit { delay(750); println("Creating UserProcessor") }
   *   fun shutdown(): Unit = println("Shutting down UserProcessor")
   *   fun process(ds: DataSource): List<String> =
   *    ds.users().map { "Processed $it" }
   * }
   *
   * class DataSource {
   *   suspend fun connect(): Unit { delay(1000); println("Connecting dataSource") }
   *   fun users(): List<String> = listOf("User-1", "User-2", "User-3")
   *   fun close(): Unit = println("Closed dataSource")
   * }
   *
   * class Service(val db: DataSource, val userProcessor: UserProcessor) {
   *   suspend fun processData(): List<String> = userProcessor.process(db)
   * }
   *
   * val userProcessor = resource {
   *   UserProcessor().also { it.start() }
   * } release UserProcessor::shutdown
   *
   * val dataSource = resource {
   *   DataSource().also { it.connect() }
   * } release DataSource::close
   *
   * suspend fun main(): Unit {
   *   userProcessor.parZip(dataSource) { userProcessor, ds ->
   *       Service(ds, userProcessor)
   *     }.use { service -> service.processData() }
   * }
   * ```
   * <!--- KNIT example-resource-09.kt -->
   */
  @Deprecated(
    "parZip $nextVersionRemoved",
    ReplaceWith(
      "resource { parZip<A, B, C>(ctx, { this.bind() }, { fb.bind() }) { a, b -> f(a, b) } }",
      "arrow.fx.coroutines.resource"
    )
  )
  public fun <B, C> parZip(
    ctx: CoroutineContext = Dispatchers.Default,
    fb: Resource<B>,
    f: suspend (A, B) -> C,
  ): Resource<C> =
    resource {
      parZip(ctx, { this@Resource.bind() }, { fb.bind() }) { a, b -> f(a, b) }
    }

  @Deprecated("Use the safer version allocate instead.")
  @DelicateCoroutinesApi
  public suspend fun allocated(): Pair<suspend () -> A, suspend (@UnsafeVariance A, ExitCase) -> Unit> =
    when (this) {
      is Bind<*, A> ->
        Dsl {
          val any = source.bind()
          val ff = f as (Any?) -> Resource<A>
          ff(any).bind()
        }.allocated()

      is Allocate -> acquire to release
      is Defer -> resource().allocated()
      is Dsl -> {
        val effect = ResourceScopeImpl()
        val allocated = try {
          val allocate: suspend () -> A = suspend { dsl(effect) }
          val release: suspend (A, ExitCase) -> Unit = { _, e ->
            effect.finalizers.get().cancelAll(e)?.let { throw it }
          }
          allocate to release
        } catch (e: Throwable) {
          val ee = withContext(NonCancellable) {
            effect.finalizers.get().cancelAll(ExitCase(e), e) ?: e
          }
          throw ee
        }
        allocated
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
   * <!--- KNIT example-resource-10.kt -->
   *
   * This is a **delicate** API. It is easy to accidentally create resource or memory leaks `allocated` is used.
   * A `Resource` allocated by `allocate` is not subject to the guarantees that [Resource] makes,
   * instead the caller is responsible for correctly invoking the `release` handler at the appropriate time.
   * This API is useful for building inter-op APIs between [Resource] and non-suspending code, such as Java libraries.
   */
  @DelicateCoroutinesApi
  public suspend fun allocate(): Pair<A, suspend (ExitCase) -> Unit> =
    when (this) {
      is Bind<*, A> ->
        Dsl {
          val any = source.bind()
          val ff = f as (Any?) -> Resource<A>
          ff(any).bind()
        }.allocate()

      is Allocate -> {
        val a = acquire()
        Pair(a) { exitCase -> release(a, exitCase) }
      }

      is Defer -> resource().allocate()
      is Dsl -> {
        val effect = ResourceScopeImpl()
        val allocated: A = dsl(effect)
        val release: suspend (ExitCase) -> Unit = { e ->
          val suppressed: Throwable? = effect.finalizers.get().cancelAll(e)
          val original: Throwable? = when (e) {
            ExitCase.Completed -> null
            is ExitCase.Cancelled -> e.exception
            is ExitCase.Failure -> e.failure
          }
          Platform.composeErrors(original, suppressed)?.let { throw it }
        }
        Pair(allocated, release)
      }
    }

  @Deprecated(
    "Bind $nextVersionRemoved",
    ReplaceWith(
      "resource { f(source.bind()) }",
      "arrow.fx.coroutines.resource"
    )
  )
  public class Bind<A, B>(public val source: Resource<A>, public val f: (A) -> Resource<B>) : Resource<B>()

  @Deprecated(
    "Allocate $nextVersionRemoved",
    ReplaceWith(
      "resource(acquire, release)",
      "arrow.fx.coroutines.resource"
    )
  )
  public class Allocate<A>(
    public val acquire: suspend () -> A,
    public val release: suspend (A, ExitCase) -> Unit,
  ) : Resource<A>()

  @Deprecated(
    "Defer $nextVersionRemoved",
    ReplaceWith(
      "resource { resource.invoke().bind() }",
      "arrow.fx.coroutines.resource"
    )
  )
  public class Defer<A>(public val resource: suspend () -> Resource<A>) : Resource<A>()

  @Deprecated(
    "Dsl $nextVersionRemoved",
    ReplaceWith(
      "resource { dsl() }",
      "arrow.fx.coroutines.resource"
    )
  )
  public data class Dsl<A>(public val dsl: suspend ResourceScope.() -> A) : Resource<A>()

  public companion object {

    @PublishedApi
    @Deprecated("This will be removed from the binary in Arrow 2.0", level = DeprecationLevel.ERROR)
    internal val unit: Resource<Unit> = just(Unit)

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
     * <!--- KNIT example-resource-11.kt -->
     */
    @Deprecated(
      "Operator invoke is replaced with top-level function",
      ReplaceWith(
        "resource(acquire, release)",
        "arrow.fx.coroutines.resource"
      )
    )
    public operator fun <A> invoke(
      acquire: suspend () -> A,
      release: suspend (A, ExitCase) -> Unit,
    ): Resource<A> = Allocate(acquire, release)

    /**
     * Create a [Resource] from a pure value [A].
     */
    @Deprecated(
      "Use the resource DSL to create Resource values. Will be removed in Arrow 2.x.x",
      ReplaceWith(
        "resource { r }",
        "arrow.fx.coroutines.resource"
      )
    )
    public fun <A> just(r: A): Resource<A> =
      Resource({ r }, { _, _ -> Unit })

    @Deprecated(
      "defer is being deprecated. Use resource DSL instead",
      ReplaceWith(
        "resource { f().bind() }",
        "arrow.fx.coroutines.resource"
      )
    )
    public fun <A> defer(f: suspend () -> Resource<A>): Resource<A> =
      Resource.Defer(f)
  }
}

public fun <A> resource(
  acquire: suspend () -> A,
  release: suspend (A, ExitCase) -> Unit,
): Resource<A> = resource {
  install({ acquire() }, release)
}

@ScopeDSL
public suspend fun <A> resourceScope(action: suspend ResourceScope.() -> A): A =
  resource(action).use(::identity)

/**
 * Marker for `suspend () -> A` to be marked as the [Use] action of a [Resource].
 * Offers a convenient DSL to use [Resource] for simple resources.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * class File(url: String) {
 *   suspend fun open(): File = this
 *   suspend fun close(): Unit {}
 *   override fun toString(): String = "This file contains some interesting content!"
 * }
 *
 * suspend fun openFile(uri: String): File = File(uri).open()
 * suspend fun closeFile(file: File): Unit = file.close()
 * suspend fun fileToString(file: File): String = file.toString()
 *
 * suspend fun main(): Unit {
 *   val res = resource {
 *     openFile("data.json")
 *   } release { file ->
 *     closeFile(file)
 *   } use { file ->
 *     fileToString(file)
 *   }
 *
 *   println(res)
 * }
 * ```
 * <!--- KNIT example-resource-12.kt -->
 */
@Deprecated(
  "Use the resource computation DSL instead",
  ReplaceWith(
    "resource { acquire() }",
    "arrow.fx.coroutines.resource"
  )
)
public inline class Use<A>(internal val acquire: suspend () -> A)

/**
 * Marks an [acquire] operation as the [Resource.use] step of a [Resource].
 */
@Deprecated(
  "Use the resource computation DSL instead",
  ReplaceWith(
    "resource { acquire() }",
    "arrow.fx.coroutines.resource"
  ),
  level = DeprecationLevel.HIDDEN
)
public fun <A> resource(acquire: suspend () -> A): Use<A> = Use(acquire)

@ScopeDSL
public fun <A> resource(action: suspend ResourceScope.() -> A): Resource<A> =
  arrow.fx.coroutines.continuations.resource(action)

@Deprecated("Use the resource computation DSL instead")
public infix fun <A> Use<A>.release(release: suspend (A) -> Unit): Resource<A> =
  Resource(acquire) { a, _ -> release(a) }

/**
 * Composes a [release] action to a [Resource.use] action creating a [Resource].
 */
public infix fun <A> Resource<A>.release(release: suspend (A) -> Unit): Resource<A> =
  resource {
    val a = bind()
    Resource({ a }, { _, _ -> release(a) }).bind()
  }

@Deprecated("Use the resource computation DSL instead")
public infix fun <A> Use<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): Resource<A> =
  Resource(acquire, release)

/**
 * Composes a [releaseCase] action to a [Resource.use] action creating a [Resource].
 */
public infix fun <A> Resource<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): Resource<A> =
  resource {
    val a = bind()
    Resource({ a }, { _, ex -> release(a, ex) }).bind()
  }

@Deprecated("traverseResource is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(f)"))
public inline fun <A, B> Iterable<A>.traverseResource(crossinline f: (A) -> Resource<B>): Resource<List<B>> =
  resource {
    map { a ->
      f(a).bind()
    }
  }

/**
 * Traverse this [Iterable] and collects the resulting `Resource<B>` of [f] into a `Resource<List<B>>`.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * class File(url: String) {
 *   suspend fun open(): File = this
 *   suspend fun close(): Unit {}
 *   override fun toString(): String = "This file contains some interesting content!"
 * }
 *
 * suspend fun openFile(uri: String): File = File(uri).open()
 * suspend fun closeFile(file: File): Unit = file.close()
 * suspend fun fileToString(file: File): String = file.toString()
 *
 * suspend fun main(): Unit {
 *   val res: List<String> = listOf(
 *     "data.json",
 *     "user.json",
 *     "resource.json"
 *   ).traverse { uri ->
 *     resource {
 *      openFile(uri)
 *     } release { file ->
 *       closeFile(file)
 *     }
 *   }.use { files ->
 *     files.map { fileToString(it) }
 *   }
 *   res.forEach(::println)
 * }
 * ```
 * <!--- KNIT example-resource-13.kt -->
 */
@Deprecated(
  "Use the resource computation DSL instead",
  ReplaceWith(
    "resource { map { a -> f(a).bind() } }",
    "arrow.fx.coroutines.resource"
  )
)
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <A, B> Iterable<A>.traverse(crossinline f: (A) -> Resource<B>): Resource<List<B>> =
  resource { map { a -> f(a).bind() } }

/**
 * Sequences this [Iterable] of [Resource]s.
 * [Iterable.map] and [sequence] is equivalent to [traverse].
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * class File(url: String) {
 *   suspend fun open(): File = this
 *   suspend fun close(): Unit {}
 *   override fun toString(): String = "This file contains some interesting content!"
 * }
 *
 * suspend fun openFile(uri: String): File = File(uri).open()
 * suspend fun closeFile(file: File): Unit = file.close()
 * suspend fun fileToString(file: File): String = file.toString()
 *
 * suspend fun main(): Unit {
 *   val res: List<String> = listOf(
 *     "data.json",
 *     "user.json",
 *     "resource.json"
 *   ).map { uri ->
 *     resource {
 *      openFile(uri)
 *     } release { file ->
 *       closeFile(file)
 *     }
 *   }.sequence().use { files ->
 *     files.map { fileToString(it) }
 *   }
 *   res.forEach(::println)
 * }
 * ```
 * <!--- KNIT example-resource-14.kt -->
 */
@Deprecated(
  "Use the resource computation DSL instead",
  ReplaceWith(
    "resource { map { a -> a.bind() } }",
    "arrow.fx.coroutines.resource"
  )
)
@Suppress("NOTHING_TO_INLINE")
public inline fun <A> Iterable<Resource<A>>.sequence(): Resource<List<A>> =
  resource { map { a -> a.bind() } }

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
  val finalizers: Atomic<List<suspend (ExitCase) -> Unit>> = Atomic(emptyList())
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
          (e?.apply { e2?.let(::addSuppressed) } ?: e2)?.let { throw it }
        }
      })

      is Resource.Bind<*, *> -> {
        val dsl: suspend ResourceScope.() -> A = {
          val any = source.bind()
          val ff = f as (Any?) -> Resource<A>
          ff(any).bind()
        }
        dsl(this@ResourceScopeImpl)
      }

      is Resource.Defer -> resource().bind()
    }

  override suspend fun <A> install(acquire: suspend AcquireStep.() -> A, release: suspend (A, ExitCase) -> Unit): A =
    bracketCase({
      val a = acquire(AcquireStep)
      val finalizer: suspend (ExitCase) -> Unit = { ex: ExitCase -> release(a, ex) }
      finalizers.update(finalizer::prependTo)
      a
    }, ::identity, { a, ex ->
      // Only if ExitCase.Failure, or ExitCase.Cancelled during acquire we cancel
      // Otherwise we've saved the finalizer, and it will be called from somewhere else.
      if (ex != ExitCase.Completed) {
        val e = finalizers.get().cancelAll(ex)
        val e2 = kotlin.runCatching { release(a, ex) }.exceptionOrNull()
        Platform.composeErrors(e, e2)?.let { throw it }
      }
    })

  override fun <A> autoClose(acquire: () -> A, release: (A, Throwable?) -> Unit): A =
    try {
      acquire().also { a ->
        val finalizer: suspend (ExitCase) -> Unit = { exitCase ->
          val errorOrNull = when (exitCase) {
            Completed -> null
            is Cancelled -> exitCase.exception
            is Failure -> exitCase.failure
          }
          release(a, errorOrNull)
        }
        finalizers.update { prev -> prev + finalizer }
      }
    } catch (e: Throwable) {
      throw e
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

@PublishedApi internal const val nextVersionRemoved: String =
  "is redundant and will be removed in Arrow 2.x.x in favor of the DSL.\n" +
    "In case you think this method should stay, please provide feedback and your use-case on https://github.com/arrow-kt/arrow/issues"
