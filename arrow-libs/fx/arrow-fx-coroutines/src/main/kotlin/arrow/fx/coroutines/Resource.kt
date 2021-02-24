package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.identity
import java.io.Closeable
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
 * However this program is not correct. It does not guarantee release if something failed in between, and keeping track of acquisition order is unnecessary overhead.
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

  class Bind<A, B>(val source: Resource<A>, val f: (A) -> Resource<B>) : Resource<B>()

  class Allocate<A>(
    val acquire: suspend () -> A,
    val release: suspend (A, ExitCase) -> Unit
  ) : Resource<A>()

  class Defer<A>(val resource: suspend () -> Resource<A>) : Resource<A>()

  companion object {
    val unit: Resource<Unit> = just(Unit)

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
     * Creates a [Resource] from an [Closeable], which uses [Closeable.close] for releasing.
     *
     * ```kotlin:ank:playground
     * import arrow.fx.coroutines.*
     * import java.io.FileInputStream
     *
     * suspend fun copyFile(src: String, dest: String): Unit =
     *   Resource.fromClosable { FileInputStream(src) }
     *     .zip(Resource.fromClosable { FileInputStream(dest) })
     *     .use { (a: FileInputStream, b: FileInputStream) ->
     *        /** read from [a] and write to [b]. **/
     *        // Both resources will be closed accordingly to their #close methods
     *     }
     * ```
     */
    fun <A : Closeable> fromClosable(f: suspend () -> A): Resource<A> =
      Resource(f) { s -> s.close() }

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

    inline fun <B, C, D> mapN(
      b: Resource<B>,
      c: Resource<C>,
      crossinline map: (B, C) -> D
    ): Resource<D> =
      mapN(b, c, unit, unit, unit, unit, unit, unit, unit, unit) { b, c, d, _, _, _, _, _, _, _ ->
        map(b, c)
      }

    inline fun <B, C, D, E> mapN(
      b: Resource<B>,
      c: Resource<C>,
      d: Resource<D>,
      crossinline map: (B, C, D) -> E
    ): Resource<E> =
      mapN(b, c, d, unit, unit, unit, unit, unit, unit, unit) { b, c, d, _, _, _, _, _, _, _ ->
        map(b, c, d)
      }

    inline fun <B, C, D, E, F> mapN(
      b: Resource<B>,
      c: Resource<C>,
      d: Resource<D>,
      e: Resource<E>,
      crossinline map: (B, C, D, E) -> F
    ): Resource<F> =
      mapN(b, c, d, e, unit, unit, unit, unit, unit, unit) { b, c, d, e, _, _, _, _, _, _ ->
        map(b, c, d, e)
      }

    inline fun <B, C, D, E, F, G> mapN(
      b: Resource<B>,
      c: Resource<C>,
      d: Resource<D>,
      e: Resource<E>,
      f: Resource<F>,
      crossinline map: (B, C, D, E, F) -> G
    ): Resource<G> =
      mapN(b, c, d, e, f, unit, unit, unit, unit, unit) { b, c, d, e, f, _, _, _, _, _ ->
        map(b, c, d, e, f)
      }

    inline fun <B, C, D, E, F, G, H> mapN(
      b: Resource<B>,
      c: Resource<C>,
      d: Resource<D>,
      e: Resource<E>,
      f: Resource<F>,
      g: Resource<G>,
      crossinline map: (B, C, D, E, F, G) -> H
    ): Resource<H> =
      mapN(b, c, d, e, f, g, unit, unit, unit, unit) { b, c, d, e, f, g, _, _, _, _ ->
        map(b, c, d, e, f, g)
      }

    inline fun <B, C, D, E, F, G, H, I> mapN(
      b: Resource<B>,
      c: Resource<C>,
      d: Resource<D>,
      e: Resource<E>,
      f: Resource<F>,
      g: Resource<G>,
      h: Resource<H>,
      crossinline map: (B, C, D, E, F, G, H) -> I
    ): Resource<I> =
      mapN(b, c, d, e, f, g, h, unit, unit, unit) { b, c, d, e, f, g, h, _, _, _ ->
        map(b, c, d, e, f, g, h)
      }

    inline fun <B, C, D, E, F, G, H, I, J> mapN(
      b: Resource<B>,
      c: Resource<C>,
      d: Resource<D>,
      e: Resource<E>,
      f: Resource<F>,
      g: Resource<G>,
      h: Resource<H>,
      i: Resource<I>,
      crossinline map: (B, C, D, E, F, G, H, I) -> J
    ): Resource<J> =
      mapN(b, c, d, e, f, g, h, i, unit, unit) { b, c, d, e, f, g, h, i, _, _ ->
        map(b, c, d, e, f, g, h, i)
      }

    inline fun <B, C, D, E, F, G, H, I, J, K> mapN(
      b: Resource<B>,
      c: Resource<C>,
      d: Resource<D>,
      e: Resource<E>,
      f: Resource<F>,
      g: Resource<G>,
      h: Resource<H>,
      i: Resource<I>,
      j: Resource<J>,
      crossinline map: (B, C, D, E, F, G, H, I, J) -> K
    ): Resource<K> =
      mapN(b, c, d, e, f, g, h, i, j, unit) { b, c, d, e, f, g, h, i, j, _ ->
        map(b, c, d, e, f, g, h, i, j)
      }

    inline fun <B, C, D, E, F, G, H, I, J, K, L> mapN(
      b: Resource<B>,
      c: Resource<C>,
      d: Resource<D>,
      e: Resource<E>,
      f: Resource<F>,
      g: Resource<G>,
      h: Resource<H>,
      i: Resource<I>,
      j: Resource<J>,
      k: Resource<K>,
      crossinline map: (B, C, D, E, F, G, H, I, J, K) -> L
    ): Resource<L> =
      b.flatMap { bb ->
        c.flatMap { cc ->
          d.flatMap { dd ->
            e.flatMap { ee ->
              f.flatMap { ff ->
                g.flatMap { gg ->
                  h.flatMap { hh ->
                    i.flatMap { ii ->
                      j.flatMap { jj ->
                        k.map { kk ->
                          map(bb, cc, dd, ee, ff, gg, hh, ii, jj, kk)
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
inline fun <A, B> Iterable<A>.traverseResource(crossinline f: (A) -> Resource<B>): Resource<List<B>> =
  fold(Resource.just(emptyList())) { acc: Resource<List<B>>, a: A ->
    f(a).ap(acc.map { { b: B -> it + b } })
  }

/**
 * Traverses and filters nullable resources
 * @see traverseResource
 */
inline fun <A, B> Iterable<A>.traverseFilterResource(crossinline f: (A) -> Resource<B?>): Resource<List<B>> =
  traverseResource(f).map { it.filterNotNull() }

/**
 * Traverse this [Iterable] and flattens the resulting `Resource<List<B>>` of [f] into a `Resource<List<B>>`.
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
 *   ).flatTraverseResource { uri ->
 *     resource {
 *      listOf(openFile(uri))
 *     } release { files ->
 *       files.forEach { closeFile(it) }
 *     }
 *   }.use { files ->
 *     files.map { fileToString(it) }
 *   }
 *   //sampleEnd
 *   res.forEach(::println)
 * }
 * ```
 */
inline fun <A, B> Iterable<A>.flatTraverseResource(crossinline f: (A) -> Resource<List<B>>): Resource<List<B>> =
  traverseResource(f).map { it.flatten() }

/**
 * Traverse this [Iterable] and flattens and filters out nullable elements of the resulting `Resource<List<B?>>` in [f] into a `Resource<List<B>>`.
 * @see flatTraverseResource
 */
inline fun <A, B> Iterable<A>.flatTraverseFilterResource(crossinline f: (A) -> Resource<List<B?>>): Resource<List<B>> =
  flatTraverseResource { f(it).map { list -> list.filterNotNull() } }

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
inline fun <A> Iterable<Resource<A>>.sequence(): Resource<List<A>> =
  traverseResource(::identity)

/**
 * Sequences this [Iterable] and flattens the [Resource] elements.
 * [Iterable.map] and [flatSequence] is equivalent to [flatTraverseResource].
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
 *      listOf(openFile(uri))
 *     } release { files ->
 *       files.forEach { closeFile(it) }
 *     }
 *   }.flatSequence().use { files ->
 *     files.map { fileToString(it) }
 *   }
 *   //sampleEnd
 *   res.forEach(::println)
 * }
 * ```
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <A> Iterable<Resource<Iterable<A>>>.flatSequence(): Resource<List<A>> =
  sequence().map { it.flatten() }
