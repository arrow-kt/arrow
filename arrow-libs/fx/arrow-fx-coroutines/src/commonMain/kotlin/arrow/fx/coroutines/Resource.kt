package arrow.fx.coroutines

import arrow.core.continuations.AtomicRef
import arrow.core.continuations.update
import arrow.core.identity
import arrow.core.prependTo
import arrow.fx.coroutines.continuations.ResourceScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.experimental.ExperimentalTypeInference

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
  @Suppress("UNCHECKED_CAST")
  public tailrec suspend infix fun <B> use(f: suspend (A) -> B): B =
    when (this) {
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

      is Allocate -> bracketCase(acquire, f, release)
      is Bind<*, *> -> Dsl {
        val any = source.bind()
        val ff = this@Resource.f as (Any?) -> Resource<A>
        ff(any).bind()
      }.use(f)

      is Defer -> resource().use(f)
    }

  public fun <B> map(f: suspend (A) -> B): Resource<B> =
    arrow.fx.coroutines.continuations.resource { f(bind()) }

  /** Useful for setting up/configuring an acquired resource */
  public fun tap(f: suspend (A) -> Unit): Resource<A> =
    arrow.fx.coroutines.continuations.resource { bind().also { f(it) } }

  public fun <B> ap(ff: Resource<(A) -> B>): Resource<B> =
    arrow.fx.coroutines.continuations.resource {
      val a = bind()
      ff.bind()(a)
    }

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
   * <!--- KNIT example-resource-05.kt -->
   *
   * @see zip to combine independent resources together
   * @see parZip for combining independent resources in parallel
   */
  public fun <B> flatMap(f: (A) -> Resource<B>): Resource<B> = arrow.fx.coroutines.continuations.resource {
    f(bind()).bind()
  }

  public inline fun <B, C> zip(other: Resource<B>, crossinline combine: (A, B) -> C): Resource<C> =
    arrow.fx.coroutines.continuations.resource {
      combine(bind(), other.bind())
    }

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
   * <!--- KNIT example-resource-06.kt -->
   *
   * @see parZip if you want to combine independent resources in parallel
   * @see flatMap to combine resources that rely on each-other.
   */
  public inline fun <B, C, D> zip(
    b: Resource<B>,
    c: Resource<C>,
    crossinline map: (A, B, C) -> D
  ): Resource<D> =
    arrow.fx.coroutines.continuations.resource {
      map(bind(), b.bind(), c.bind())
    }

  public inline fun <B, C, D, E> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    crossinline map: (A, B, C, D) -> E
  ): Resource<E> =
    arrow.fx.coroutines.continuations.resource {
      map(bind(), b.bind(), c.bind(), d.bind())
    }

  public inline fun <B, C, D, E, G> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    crossinline map: (A, B, C, D, E) -> G
  ): Resource<G> =
    arrow.fx.coroutines.continuations.resource {
      map(bind(), b.bind(), c.bind(), d.bind(), e.bind())
    }

  public inline fun <B, C, D, E, F, G, H> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    f: Resource<F>,
    crossinline map: (A, B, C, D, E, F) -> G
  ): Resource<G> =
    arrow.fx.coroutines.continuations.resource {
      map(bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind())
    }

  public inline fun <B, C, D, E, F, G, H> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    f: Resource<F>,
    g: Resource<G>,
    crossinline map: (A, B, C, D, E, F, G) -> H
  ): Resource<H> =
    arrow.fx.coroutines.continuations.resource {
      map(bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind())
    }

  public inline fun <B, C, D, E, F, G, H, I> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    f: Resource<F>,
    g: Resource<G>,
    h: Resource<H>,
    crossinline map: (A, B, C, D, E, F, G, H) -> I
  ): Resource<I> =
    arrow.fx.coroutines.continuations.resource {
      map(bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind())
    }

  public inline fun <B, C, D, E, F, G, H, I, J> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    f: Resource<F>,
    g: Resource<G>,
    h: Resource<H>,
    i: Resource<I>,
    crossinline map: (A, B, C, D, E, F, G, H, I) -> J
  ): Resource<J> =
    arrow.fx.coroutines.continuations.resource {
      map(bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind())
    }

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
    crossinline map: (A, B, C, D, E, F, G, H, I, J) -> K
  ): Resource<K> =
    arrow.fx.coroutines.continuations.resource {
      map(bind(), b.bind(), c.bind(), d.bind(), e.bind(), f.bind(), g.bind(), h.bind(), i.bind(), j.bind())
    }

  public fun <B, C> parZip(fb: Resource<B>, f: suspend (A, B) -> C): Resource<C> =
    parZip(Dispatchers.Default, fb, f)

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
   * <!--- KNIT example-resource-07.kt -->
   */
  public fun <B, C> parZip(
    ctx: CoroutineContext = Dispatchers.Default,
    fb: Resource<B>,
    f: suspend (A, B) -> C
  ): Resource<C> =
    arrow.fx.coroutines.continuations.resource {
      parZip(ctx, { this@Resource.bind() }, { fb.bind() }) { a, b -> f(a, b) }
    }

  /**
   * Decomposes a [Resource]<A> into a Pair<suspend () -> A, suspend (A, ExitCase) -> Unit>, containing
   * the allocation and release functions which make up the [Resource].
   *
   * This can be used to integrate Resources with code which cannot be run within the [use] function.
   */
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
          val ex = if (e is CancellationException) ExitCase.Cancelled(e) else ExitCase.Failure(e)
          val ee = withContext(NonCancellable) {
            effect.finalizers.get().cancelAll(ex, e) ?: e
          }
          throw ee
        }
        allocated
      }
    }

  @Deprecated(
    "Bind is being deprecated. Use resource DSL instead",
    ReplaceWith(
      "resource { f(source.bind()) }",
      "arrow.fx.coroutines.continuations.resource"
    )
  )
  public class Bind<A, B>(public val source: Resource<A>, public val f: (A) -> Resource<B>) : Resource<B>()

  public class Allocate<A>(
    public val acquire: suspend () -> A,
    public val release: suspend (A, ExitCase) -> Unit
  ) : Resource<A>()

  @Deprecated(
    "Defer is being deprecated. Use resource DSL instead",
    ReplaceWith(
      "resource { resource.invoke().bind() }",
      "arrow.fx.coroutines.continuations.resource"
    )
  )
  public class Defer<A>(public val resource: suspend () -> Resource<A>) : Resource<A>()

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
     * <!--- KNIT example-resource-08.kt -->
     */
    public operator fun <A> invoke(
      acquire: suspend () -> A,
      release: suspend (A, ExitCase) -> Unit
    ): Resource<A> = Allocate(acquire, release)

    /**
     * Create a [Resource] from a pure value [A].
     */
    public fun <A> just(r: A): Resource<A> =
      Resource({ r }, { _, _ -> Unit })

    @Deprecated(
      "defer is being deprecated. Use resource DSL instead",
      ReplaceWith(
        "resource { f().bind() }",
        "arrow.fx.coroutines.continuations.resource"
      )
    )
    public fun <A> defer(f: suspend () -> Resource<A>): Resource<A> =
      Resource.Defer(f)
  }
}

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
 * <!--- KNIT example-resource-09.kt -->
 */
@Deprecated(
  "Use the resource computation DSL instead",
  ReplaceWith(
    "resource { acquire() }",
    "arrow.fx.coroutines.continuations.resource"
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
    "arrow.fx.coroutines.continuations.resource"
  )
)
public fun <A> resource(acquire: suspend () -> A): Use<A> = Use(acquire)

@Deprecated("Use the resource computation DSL instead")
public infix fun <A> Use<A>.release(release: suspend (A) -> Unit): Resource<A> =
  Resource(acquire) { a, _ -> release(a) }

/**
 * Composes a [release] action to a [Resource.use] action creating a [Resource].
 */
public infix fun <A> Resource<A>.release(release: suspend (A) -> Unit): Resource<A> =
  arrow.fx.coroutines.continuations.resource {
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
  arrow.fx.coroutines.continuations.resource {
    val a = bind()
    Resource({ a }, { _, ex -> release(a, ex) }).bind()
  }

@Deprecated("traverseResource is being renamed to traverse to simplify the Arrow API", ReplaceWith("traverse(f)"))
public inline fun <A, B> Iterable<A>.traverseResource(crossinline f: (A) -> Resource<B>): Resource<List<B>> =
  arrow.fx.coroutines.continuations.resource {
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
 * <!--- KNIT example-resource-10.kt -->
 */
@OptIn(ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
public inline fun <A, B> Iterable<A>.traverse(crossinline f: (A) -> Resource<B>): Resource<List<B>> =
  arrow.fx.coroutines.continuations.resource {
    map { a ->
      f(a).bind()
    }
  }

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
 * <!--- KNIT example-resource-11.kt -->
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <A> Iterable<Resource<A>>.sequence(): Resource<List<A>> =
  traverse(::identity)

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
}

private suspend fun List<suspend (ExitCase) -> Unit>.cancelAll(
  exitCase: ExitCase,
  first: Throwable? = null
): Throwable? = fold(first) { acc, finalizer ->
  val other = kotlin.runCatching { finalizer(exitCase) }.exceptionOrNull()
  other?.let {
    acc?.apply { addSuppressed(other) } ?: other
  } ?: acc
}
