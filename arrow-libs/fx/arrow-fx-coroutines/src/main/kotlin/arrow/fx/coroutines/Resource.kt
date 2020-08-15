package arrow.fx.coroutines

import arrow.core.Either
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

/**
 * [Resource] models resource allocation and releasing. It is especially useful when multiple resources that depend on each other
 *  need to be acquired and later released in reverse order.
 *
 * When a resource is created one can make use of [use] to run a computation with the resource. The finalizers are then
 *  guaranteed to run afterwards in reverse order of acquisition.
 *
 * Consider the following use case:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * object Consumer
 * object Handle
 *
 * class Service(val handle: Handle, val consumer: Consumer)
 *
 * suspend fun createConsumer(): Consumer = Consumer.also { println("Creating consumer") }
 * suspend fun createDBHandle(): Handle = Handle.also { println("Creating db handle") }
 * suspend fun createFancyService(consumer: Consumer, handle: Handle): Service =
 *   Service(handle, consumer).also { println("Creating service") }
 *
 * suspend fun closeConsumer(consumer: Consumer): Unit = println("Closed consumer")
 * suspend fun closeDBHandle(handle: Handle): Unit = println("Closed db handle")
 * suspend fun shutDownFancyService(service: Service): Unit = println("Closed service")
 *
 * //sampleStart
 * val program = suspend {
 *   val consumer = createConsumer()
 *   val handle = createDBHandle()
 *   val service = createFancyService(consumer, handle)
 *
 *   // use service
 *   // <...>
 *
 *   // we are done, now onto releasing resources
 *   shutDownFancyService(service)
 *   closeDBHandle(handle)
 *   closeConsumer(consumer)
 * }
 * //sampleEnd
 * suspend fun main(): Unit = program.invoke()
 * ```
 * Here we are creating and then using a service that has a dependency on two resources: A database handle and a consumer of some sort. All three resources need to be closed in the correct order at the end.
 * However this program is quite bad! It does not guarantee release if something failed in between and keeping track of acquisition order is unnecessary overhead.
 *
 * That is where `Resource` comes in:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * object Consumer
 * object Handle
 *
 * class Service(val handle: Handle, val consumer: Consumer)
 *
 * suspend fun createConsumer(): Consumer = Consumer.also { println("Creating consumer") }
 * suspend fun createDBHandle(): Handle = Handle.also { println("Creating db handle") }
 * suspend fun createFancyService(consumer: Consumer, handle: Handle): Service =
 *   Service(handle, consumer).also { println("Creating service") }
 *
 * suspend fun closeConsumer(consumer: Consumer): Unit = println("Closed consumer")
 * suspend fun closeDBHandle(handle: Handle): Unit = println("Closed db handle")
 * suspend fun shutDownFancyService(service: Service): Unit = println("Closed service")
 *
 * //sampleStart
 * val resourceProgram = suspend {
 *   Resource(::createConsumer, ::closeConsumer)
 *     .zip(Resource(::createDBHandle, ::closeDBHandle))
 *     .flatMap { (consumer, handle) ->
 *       Resource({ createFancyService(consumer, handle) }, { service -> shutDownFancyService(service) })
 *     }.use { service ->
 *       // use service
 *       // <...>
 *       Unit
 *     }
 * }
 * //sampleEnd
 *
 * suspend fun main(): Unit = resourceProgram.invoke()
 * ```
 *
 * All three programs do exactly the same with varying levels of simplicity and overhead. `Resource` uses `Bracket` under the hood but provides a nicer monadic interface for creating and releasing resources in order, whereas bracket is great for one-off acquisitions but becomes more complex with nested resources.
 **/
sealed class Resource<out A> {

  /**
   * Use the created resource
   * When done will run all finalizers
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun acquireResource(): Int = 42.also { println("Getting expensive resource") }
   * suspend fun releaseResource(r: Int): Unit = println("Releasing expensive resource: $r")
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   Resource(::acquireResource, ::releaseResource)
   *     .use { println("Expensive resource under use! $it") }
   *   //sampleEnd
   * }
   * ```
   */
  @Suppress("UNCHECKED_CAST")
  suspend infix fun <B> use(f: suspend (A) -> B): B =
    useLoop(this as Resource<Any?>, f as suspend (Any?) -> Any?, emptyList()) as B

  fun <B> map(f: (A) -> B): Resource<B> =
    flatMap { a -> just(f(a)) }

  fun <B> ap(ff: Resource<(A) -> B>): Resource<B> =
    flatMap { res -> ff.map { it(res) } }

  /**
   * Create a new resource [B] from a resource [A] by mapping [f].
   *
   * This is useful when you need to create a resource that depends on other resources.
   *
   * ```kotlin:ank
   * import arrow.fx.coroutines.*
   *
   * object Consumer
   * object DBHandle
   * data class DatabaseConsumer(val db: DBHandle, val consumer: Consumer)
   *
   * fun consumer(): Resource<Consumer> = Resource({ Consumer }, { _ -> println("Closed consumer") })
   * fun dhHandle(): Resource<DBHandle> = Resource({ DBHandle }, { _ -> println("Closed db handle") })
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   consumer()
   *     .zip(dhHandle())
   *     .flatMap { (consumer, dbHandle) ->
   *       Resource(
   *         { DatabaseConsumer(dbHandle, consumer) },
   *         { _ -> println("Closed DatabaseConsumer") }
   *       )
   *     }.use { println("Expensive resource under use! $it") }
   *   //sampleEnd
   * }
   * ```
   */
  fun <B> flatMap(f: (A) -> Resource<B>): Resource<B> =
    Bind(this, f)

  fun <B, C> map2(other: Resource<B>, combine: (A, B) -> C): Resource<C> =
    flatMap { r ->
      other.map { r2 -> combine(r, r2) }
    }

  fun <B> zip(other: Resource<B>): Resource<Pair<A, B>> =
    map2(other, ::Pair)

  internal class Bind<A, B>(val source: Resource<A>, val f: (A) -> Resource<B>) : Resource<B>()

  internal class Allocate<A>(
    val acquire: suspend () -> A,
    val release: suspend (A, ExitCase) -> Unit
  ) : Resource<A>()

  internal class Defer<A>(val resource: suspend () -> Resource<A>) : Resource<A>()

  companion object {
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
    operator fun <A> invoke(
      acquire: suspend () -> A,
      release: suspend (A, ExitCase) -> Unit
    ): Resource<A> = Allocate(acquire, release)

    /**
     * Construct a [Resource] from a allocating function [acquire] and a release function [release].
     *
     * @see [use] For a version that provides an [ExitCase] to [release]
     */
    operator fun <A> invoke(
      acquire: suspend () -> A,
      release: suspend (A) -> Unit
    ): Resource<A> = invoke(acquire, { r, _ -> release(r) })

    /**
     * Create a [Resource] from a pure value [A].
     */
    fun <A> just(r: A): Resource<A> =
      Resource({ r }, { _, _ -> Unit })

    fun <A> defer(f: suspend () -> Resource<A>): Resource<A> =
      Resource.Defer(f)

    /**
     * Creates a single threaded [CoroutineContext] as a [Resource].
     * Upon release an orderly shutdown of the [ExecutorService] takes place in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     *
     * ```kotlin:ank:playground
     * import arrow.fx.coroutines.*
     * import java.util.concurrent.Executors
     * import java.util.concurrent.atomic.AtomicInteger
     * import kotlin.math.max
     *
     * suspend fun main(): Unit {
     *   val pool = Resource.fromExecutor {
     *     val ctr = AtomicInteger(0)
     *     val size = max(2, Runtime.getRuntime().availableProcessors())
     *     Executors.newFixedThreadPool(size) { r ->
     *       Thread(r, "computation-${ctr.getAndIncrement()}")
     *         .apply { isDaemon = true }
     *     }
     *   }
     *
     *   pool.use { ctx ->
     *     listOf(1, 2, 3, 4, 5).parTraverse(ctx) { i ->
     *       println("#$i running on ${Thread.currentThread().name}")
     *     }
     *   }
     * }
     * ```
     */
    fun fromExecutor(f: suspend () -> ExecutorService): Resource<CoroutineContext> =
      Resource(f) { s -> s.shutdown() }.map(ExecutorService::asCoroutineContext)

    /**
     * Creates a single threaded [CoroutineContext] as a [Resource].
     * Upon release an orderly shutdown of the [ExecutorService] takes place in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     *
     * ```kotlin:ank:playground
     * import arrow.fx.coroutines.*
     *
     * val singleCtx = Resource.singleThreadContext("single")
     *
     * suspend fun main(): Unit =
     *   singleCtx.use { ctx ->
     *     evalOn(ctx) {
     *       println("I am running on ${Thread.currentThread().name}")
     *     }
     *   }
     * ```
     */
    fun singleThreadContext(name: String): Resource<CoroutineContext> =
      fromExecutor {
        Executors.newSingleThreadExecutor { r ->
          Thread(r, name).apply {
            isDaemon = true
          }
        }
      }

    @Suppress("UNCHECKED_CAST")
    fun <A, B> tailRecM(a: A, f: (A) -> Resource<Either<A, B>>): Resource<B> {
      fun loop(r: Resource<Either<A, B>>): Resource<B> = when (r) {
        is Bind<*, *> -> Bind(
          r.source as Resource<A>,
          (r.f as (A) -> Resource<Either<A, B>>).andThen(::loop)
        )
        is Allocate -> Defer {
          val res = r.acquire.invoke()
          when (res) {
            is Either.Left -> {
              r.release(res, ExitCase.Completed)
              tailRecM(res.a, f)
            }
            is Either.Right -> Allocate({ res.b }, { _, ec -> r.release(res, ec) })
          }
        }
        is Defer -> Defer { loop(r.resource.invoke()) }
      }

      return loop(f(a))
    }
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
 * ```kotlin:ank
 * import arrow.fx.coroutines.*
 *
 * class File(url: String) {
 *   fun open(): File = this
 *   fun close(): Unit {}
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
inline class Use<A>(internal val acquire: suspend () -> A)

/**
 * Marks an [acquire] operation as the [Resource.use] step of a [Resource].
 */
fun <A> resource(acquire: suspend () -> A): Use<A> = Use(acquire)

/**
 * Composes a [release] action to a [Resource.use] action creating a [Resource].
 */
infix fun <A> Use<A>.release(release: suspend (A) -> Unit): Resource<A> =
  Resource(acquire, release)

/**
 * Composes a [releaseCase] action to a [Resource.use] action creating a [Resource].
 */
infix fun <A> Use<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): Resource<A> =
  Resource(acquire, release)
