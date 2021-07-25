package arrow.fx.coroutines

import arrow.core.Either
import arrow.core.andThen
import arrow.core.identity

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
public sealed class Resource<out A> {

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
  public suspend infix fun <B> use(f: suspend (A) -> B): B =
    useLoop(this as Resource<Any?>, f as suspend (Any?) -> Any?, emptyList()) as B

  public fun <B> map(f: (A) -> B): Resource<B> =
    flatMap { a -> just(f(a)) }

  public fun <B> ap(ff: Resource<(A) -> B>): Resource<B> =
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
  public fun <B> flatMap(f: (A) -> Resource<B>): Resource<B> =
    Bind(this, f)

  public fun <B, C> zip(other: Resource<B>, combine: (A, B) -> C): Resource<C> =
   flatMap { r ->
      other.map { r2 -> combine(r, r2) }
    }

  public fun <B> zip(other: Resource<B>): Resource<Pair<A, B>> =
    zip(other, ::Pair)

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
     * Construct a [Resource] from a allocating function [acquire] and a release function [release].
     *
     * @see [use] For a version that provides an [ExitCase] to [release]
     */
    public operator fun <A> invoke(
      acquire: suspend () -> A,
      release: suspend (A) -> Unit
    ): Resource<A> = invoke(acquire, { r, _ -> release(r) })

    /**
     * Create a [Resource] from a pure value [A].
     */
    public fun <A> just(r: A): Resource<A> =
      Resource({ r }, { _, _ -> Unit })

    public fun <A> defer(f: suspend () -> Resource<A>): Resource<A> =
      Resource.Defer(f)

    @Suppress("UNCHECKED_CAST")
    public fun <A, B> tailRecM(a: A, f: (A) -> Resource<Either<A, B>>): Resource<B> {
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
              tailRecM(res.value, f)
            }
            is Either.Right -> Allocate({ res.value }, { _, ec -> r.release(res, ec) })
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
public inline class Use<A>(internal val acquire: suspend () -> A)

/**
 * Marks an [acquire] operation as the [Resource.use] step of a [Resource].
 */
public fun <A> resource(acquire: suspend () -> A): Use<A> = Use(acquire)

/**
 * Composes a [release] action to a [Resource.use] action creating a [Resource].
 */
public infix fun <A> Use<A>.release(release: suspend (A) -> Unit): Resource<A> =
  Resource(acquire, release)

/**
 * Composes a [releaseCase] action to a [Resource.use] action creating a [Resource].
 */
public infix fun <A> Use<A>.releaseCase(release: suspend (A, ExitCase) -> Unit): Resource<A> =
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
