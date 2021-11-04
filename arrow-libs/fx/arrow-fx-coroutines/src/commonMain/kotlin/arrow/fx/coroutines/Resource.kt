package arrow.fx.coroutines

import arrow.core.identity
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable



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
 * ```kotlin:ank:playground
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
 * //sampleStart
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
 * //sampleEnd
 * ```
 * In the following example, we are creating and using a service that has a dependency on two resources: A database and a processor. All resources need to be closed in the correct order at the end.
 * However this program is not safe because it is prone to leaking `dataSource` and `userProcessor` when an exception or cancellation signal occurs whilst using the service.
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
 *
 * Here `releaseCase` also signals with what [ExitCase] state the `use` step finished.
 *
 * # Using and composing Resource
 *
 * ```kotlin:ank:playground
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
 * //sampleStart
 * val userProcessor = resource {
 *   UserProcessor().also(UserProcessor::start)
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
 * //sampleEnd
 * ```
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
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * class DataSource {
   *   fun connect(): Unit = println("Connecting dataSource")
   *   fun users(): List<String> = listOf("User-1", "User-2", "User-3")
   *   fun close(): Unit = println("Closed dataSource")
   * }
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val dataSource = resource {
   *     DataSource().also { it.connect() }
   *   } release DataSource::close
   *
   *   val res = dataSource
   *     .use { ds -> "Using data source: ${ds.users()}" }
   *     .also(::println)
   *   //sampleEnd
   * }
   * ```
   */
  @Suppress("UNCHECKED_CAST")
  public suspend infix fun <B> use(f: suspend (A) -> B): B =
    useLoop(this as Resource<Any?>, f as suspend (Any?) -> Any?, emptyList()) as B

  public fun <B> map(f: suspend (A) -> B): Resource<B> =
    flatMap { a -> Resource({ f(a) }) { _, _ -> } }

  /** Useful for setting up/configuring an acquired resource */
  public fun <B> tap(f: suspend (A) -> Unit): Resource<A> =
    map { f(it); it }

  public fun <B> ap(ff: Resource<(A) -> B>): Resource<B> =
    flatMap { res -> ff.map { it(res) } }

  /**
   * Create a resource value of [B] from a resource [A] by mapping [f].
   *
   * Useful when there is a need to create resources that depend on other resources,
   * for combining independent values [zip] provides nicer syntax without the need for callback nesting.
   *
   * ```kotlin:ank
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
   *   //sampleStart
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
   *   //sampleEnd
   * }
   * ```
   *
   * @see zip to combine independent resources together
   * @see parZip for combining independent resources in parallel
   */
  public fun <B> flatMap(f: (A) -> Resource<B>): Resource<B> =
    Bind(this, f)

  public inline fun <B, C> zip(other: Resource<B>, crossinline combine: (A, B) -> C): Resource<C> =
    flatMap { r ->
      other.map { r2 -> combine(r, r2) }
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
   * ```kotlin:ank:playground
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
   * //sampleStart
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
   * //sampleEnd
   * ```
   *
   * @see parZip if you want to combine independent resources in parallel
   * @see flatMap to combine resources that rely on each-other.
   */
  public inline fun <B, C, D> zip(
    b: Resource<B>,
    c: Resource<C>,
    crossinline map: (A, B, C) -> D
  ): Resource<D> =
    zip(b, c, unit, unit, unit, unit, unit, unit, unit) { a, b, c, _, _, _, _, _, _, _ ->
      map(a, b, c)
    }

  public inline fun <B, C, D, E> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    crossinline map: (A, B, C, D) -> E
  ): Resource<E> =
    zip(b, c, d, unit, unit, unit, unit, unit, unit) { a, b, c, d, e, _, _, _, _, _ ->
      map(a, b, c, d)
    }

  public inline fun <B, C, D, E, F, G> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    crossinline map: (A, B, C, D, E) -> G
  ): Resource<G> =
    zip(b, c, d, e, unit, unit, unit, unit, unit) { a, b, c, d, e, _, _, _, _, _ ->
      map(a, b, c, d, e)
    }

  public inline fun <B, C, D, E, F, G, H> zip(
    b: Resource<B>,
    c: Resource<C>,
    d: Resource<D>,
    e: Resource<E>,
    f: Resource<F>,
    crossinline map: (A, B, C, D, E, F) -> G
  ): Resource<G> =
    zip(b, c, d, e, f, unit, unit, unit, unit) { b, c, d, e, f, g, _, _, _, _ ->
      map(b, c, d, e, f, g)
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
    zip(b, c, d, e, f, g, unit, unit, unit) { a, b, c, d, e, f, g, _, _, _ ->
      map(a, b, c, d, e, f, g)
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
    zip(b, c, d, e, f, g, h, unit, unit) { a, b, c, d, e, f, g, h, _, _ ->
      map(a, b, c, d, e, f, g, h)
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
    zip(b, c, d, e, f, g, h, i, unit) { a, b, c, d, e, f, g, h, i, _ ->
      map(a, b, c, d, e, f, g, h, i)
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
    flatMap { aa ->
      b.flatMap { bb ->
        c.flatMap { cc ->
          d.flatMap { dd ->
            e.flatMap { ee ->
              f.flatMap { ff ->
                g.flatMap { gg ->
                  h.flatMap { hh ->
                    i.flatMap { ii ->
                      j.map { jj ->
                        map(aa, bb, cc, dd, ee, ff, gg, hh, ii, jj)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

  public fun <B, C> parZip(fb: Resource<B>, f: suspend (A, B) -> C): Resource<C> =
    parZip(Dispatchers.Default, fb, f)

  /**
   * Composes two [Resource]s together by zipping them in parallel,
   * by running both their `acquire` handlers in parallel, and both `release` handlers in parallel.
   *
   * Useful in the case that starting a resource takes considerable computing resources or time.
   *
   * ```kotlin:ank:playground
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
   * //sampleStart
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
   * //sampleEnd
   * ```
   */
  public fun <B, C> parZip(
    ctx: CoroutineContext = Dispatchers.Default,
    fb: Resource<B>,
    f: suspend (A, B) -> C
  ): Resource<C> =
    arrow.fx.coroutines.computations.resource {
      parZip(ctx, { this@Resource.bind() }, { fb.bind() }) { a, b -> f(a, b) }
    }

  public class Bind<A, B>(public val source: Resource<A>, public val f: (A) -> Resource<B>) : Resource<B>()

  public class Allocate<A>(
    public val acquire: suspend () -> A,
    public val release: suspend (A, ExitCase) -> Unit
  ) : Resource<A>()

  public class Defer<A>(public val resource: suspend () -> Resource<A>) : Resource<A>()

  public companion object {

    @PublishedApi
    internal val unit: Resource<Unit> = just(Unit)

    /**
     * Construct a [Resource] from a allocating function [acquire] and a release function [release].
     *
     * ```kotlin:ank:playground
     * import arrow.fx.coroutines.*
     *
     * suspend fun acquireResource(): Int = 42.also { println("Getting expensive resource") }
     * suspend fun releaseResource(r: Int): Unit = println("Releasing expensive resource: $r")
     *
     * suspend fun main(): Unit {
     *   //sampleStart
     *   val resource = Resource(::acquireResource, ::releaseResource)
     *   resource.use {
     *     println("Expensive resource under use! $it")
     *   }
     *   //sampleEnd
     * }
     * ```
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

    public fun <A> defer(f: suspend () -> Resource<A>): Resource<A> =
      Resource.Defer(f)
  }

  private suspend fun continueLoop(
    current: Resource<Any?>,
    use: suspend (Any?) -> Any?,
    stack: List<(Any?) -> Resource<Any?>>
  ): Any? = useLoop(current, use, stack)

  // Interpreter that knows how to evaluate a Resource data structure
  // Maintains its own stack for dealing with Bind chains
  @Suppress("UNCHECKED_CAST")
  private tailrec suspend fun useLoop(
    current: Resource<Any?>,
    use: suspend (Any?) -> Any?,
    stack: List<(Any?) -> Resource<Any?>>
  ): Any? =
    when (current) {
      is Defer -> useLoop(current.resource.invoke(), use, stack)
      is Bind<*, *> ->
        useLoop(current.source as Resource<Any?>, use, listOf(current.f as (Any?) -> Resource<Any?>) + stack)
      is Allocate -> bracketCase(
        acquire = current.acquire,
        use = { a ->
          when {
            stack.isEmpty() -> use(a)
            else -> continueLoop(stack.first()(a), use, stack.drop(1))
          }
        },
        release = { a, exitCase -> current.release(a, exitCase) }
      )
    }
}

/**
 * Marker for `suspend () -> A` to be marked as the [Use] action of a [Resource].
 * Offers a convenient DSL to use [Resource] for simple resources.
 *
 * ```kotlin:ank:playground
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
 */
@Deprecated("Use the resource computation DSL instead")
public inline class Use<A>(internal val acquire: suspend () -> A)

/**
 * Marks an [acquire] operation as the [Resource.use] step of a [Resource].
 */
@Deprecated("Use the resource computation DSL instead", ReplaceWith("resource(acquire)", "arrow.fx.coroutines.computation.resource"))
public fun <A> resource(acquire: suspend () -> A): Use<A> = Use(acquire)

@Deprecated("Use the resource computation DSL instead")
public infix fun <A> Use<A>.release(release: suspend (A) -> Unit): Resource<A> =
  Resource(acquire) { a, _ -> release(a) }

/**
 * Composes a [release] action to a [Resource.use] action creating a [Resource].
 */
public infix fun <A> Resource<A>.release(release: suspend (A) -> Unit): Resource<A> =
  flatMap { a ->
    Resource({ a }, { _, _ -> release(a) })
  }

@Deprecated("Use the resource computation DSL instead")
public infix fun <A> Use<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): Resource<A> =
  Resource(acquire, release)

/**
 * Composes a [releaseCase] action to a [Resource.use] action creating a [Resource].
 */
public infix fun <A> Resource<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): Resource<A> =
  flatMap { a ->
    Resource({ a }, { _, ex -> release(a, ex) })
  }

/**
 * Traverse this [Iterable] and collects the resulting `Resource<B>` of [f] into a `Resource<List<B>>`.
 *
 * ```kotlin:ank:playground
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
 *   //sampleStart
 *   val res: List<String> = listOf(
 *     "data.json",
 *     "user.json",
 *     "resource.json"
 *   ).traverseResource { uri ->
 *     resource {
 *      openFile(uri)
 *     } release { file ->
 *       closeFile(file)
 *     }
 *   }.use { files ->
 *     files.map { fileToString(it) }
 *   }
 *   //sampleEnd
 *   res.forEach(::println)
 * }
 * ```
 */
public inline fun <A, B> Iterable<A>.traverseResource(crossinline f: (A) -> Resource<B>): Resource<List<B>> =
  fold(Resource.just(emptyList())) { acc: Resource<List<B>>, a: A ->
    f(a).ap(acc.map { { b: B -> it + b } })
  }

/**
 * Sequences this [Iterable] of [Resource]s.
 * [Iterable.map] and [sequence] is equivalent to [traverseResource].
 *
 * ```kotlin:ank:playground
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
 *   //sampleStart
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
 *   //sampleEnd
 *   res.forEach(::println)
 * }
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
public inline fun <A> Iterable<Resource<A>>.sequence(): Resource<List<A>> =
  traverseResource(::identity)
