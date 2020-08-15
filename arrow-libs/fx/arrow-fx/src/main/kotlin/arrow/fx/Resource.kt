package arrow.fx

import arrow.HkJ3
import arrow.Kind
import arrow.core.Either
import arrow.core.andThen
import arrow.core.identity
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.ExitCase
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

class ForResource private constructor() {
  companion object
}
typealias ResourceOf<F, E, A> = arrow.Kind3<ForResource, F, E, A>
typealias ResourcePartialOf<F, E> = arrow.Kind2<ForResource, F, E>
typealias ResourceKindedJ<F, E, A> = HkJ3<ForResource, F, E, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <F, E, A> ResourceOf<F, E, A>.fix(): Resource<F, E, A> =
  this as Resource<F, E, A>

/**
 * ank_macro_hierarchy(arrow.fx.Resource)
 *
 * [Resource] models resource allocation and releasing. It is especially useful when multiple resources that depend on each other
 *  need to be acquired and later released in reverse order.
 * When a resource is created one can make use of [use] to run a computation with the resource. The finalizers are then
 *  guaranteed to run afterwards in reverse order of acquisition.
 *
 * Consider the following use case:
 * ```kotlin:ank:playground
 * import arrow.fx.IO
 * import arrow.fx.extensions.fx
 *
 * object Consumer
 * object Handle
 *
 * class Service(val handle: Handle, val consumer: Consumer)
 *
 * fun createConsumer(): IO<Consumer> = IO { println("Creating consumer"); Consumer }
 * fun createDBHandle(): IO<Handle> = IO { println("Creating db handle"); Handle }
 * fun createFancyService(consumer: Consumer, handle: Handle): IO<Service> = IO { println("Creating service"); Service(handle, consumer) }
 *
 * fun closeConsumer(consumer: Consumer): IO<Unit> = IO { println("Closed consumer") }
 * fun closeDBHandle(handle: Handle): IO<Unit> = IO { println("Closed db handle") }
 * fun shutDownFancyService(service: Service): IO<Unit> = IO { println("Closed service") }
 *
 * //sampleStart
 * val program = IO.fx {
 *   val consumer = !createConsumer()
 *   val handle = !createDBHandle()
 *   val service = !createFancyService(consumer, handle)
 *
 *   // use service
 *   // <...>
 *
 *   // we are done, now onto releasing resources
 *   !shutDownFancyService(service)
 *   !closeDBHandle(handle)
 *   !closeConsumer(consumer)
 * }
 * //sampleEnd
 *
 * fun main() {
 *   program.unsafeRunSync()
 * }
 * ```
 * Here we are creating and then using a service that has a dependency on two resources: A database handle and a consumer of some sort. All three resources need to be closed in the correct order at the end.
 * However this program is quite bad! It does not guarantee release if something failed in between and keeping track of acquisition order is unnecessary overhead.
 *
 * There is already a typeclass called bracket that we can use to make our life easier:
 * ```kotlin:ank:playground
 * import arrow.fx.IO
 * import arrow.fx.extensions.io.bracket.bracket
 *
 * object Consumer
 * object Handle
 *
 * class Service(val handle: Handle, val consumer: Consumer)
 *
 * fun createConsumer(): IO<Consumer> = IO { println("Creating consumer"); Consumer }
 * fun createDBHandle(): IO<Handle> = IO { println("Creating db handle"); Handle }
 * fun createFancyService(consumer: Consumer, handle: Handle): IO<Service> = IO { println("Creating service"); Service(handle, consumer) }
 *
 * fun closeConsumer(consumer: Consumer): IO<Unit> = IO { println("Closed consumer") }
 * fun closeDBHandle(handle: Handle): IO<Unit> = IO { println("Closed db handle") }
 * fun shutDownFancyService(service: Service): IO<Unit> = IO { println("Closed service") }
 *
 * //sampleStart
 * val bracketProgram =
 *   createConsumer().bracket(::closeConsumer) { consumer ->
 *     createDBHandle().bracket(::closeDBHandle) { handle ->
 *       createFancyService(consumer, handle).bracket(::shutDownFancyService) { service ->
 *         // use service
 *         // <...>
 *         IO.unit
 *       }
 *     }
 *   }
 * //sampleEnd
 *
 * fun main() {
 *   bracketProgram.unsafeRunSync()
 * }
 * ```
 *
 * This is already much better. Now our services are guaranteed to close properly and also in order. However this pattern gets worse and worse the more resources you add because you need to nest deeper and deeper.
 *
 * That is where `Resource` comes in:
 * ```kotlin:ank:playground
 * import arrow.fx.IO
 * import arrow.fx.Resource
 * import arrow.fx.extensions.resource.monad.monad
 * import arrow.fx.extensions.io.bracket.bracket
 * import arrow.fx.fix
 *
 * object Consumer
 * object Handle
 *
 * class Service(val handle: Handle, val consumer: Consumer)
 *
 * fun createConsumer(): IO<Consumer> = IO { println("Creating consumer"); Consumer }
 * fun createDBHandle(): IO<Handle> = IO { println("Creating db handle"); Handle }
 * fun createFancyService(consumer: Consumer, handle: Handle): IO<Service> = IO { println("Creating service"); Service(handle, consumer) }
 *
 * fun closeConsumer(consumer: Consumer): IO<Unit> = IO { println("Closed consumer") }
 * fun closeDBHandle(handle: Handle): IO<Unit> = IO { println("Closed db handle") }
 * fun shutDownFancyService(service: Service): IO<Unit> = IO { println("Closed service") }
 *
 * //sampleStart
 * val managedTProgram = Resource.monad(IO.bracket()).fx.monad {
 *   val consumer = Resource(::createConsumer, ::closeConsumer, IO.bracket()).bind()
 *   val handle = Resource(::createDBHandle, ::closeDBHandle, IO.bracket()).bind()
 *   Resource({ createFancyService(consumer, handle) }, ::shutDownFancyService, IO.bracket()).bind()
 * }.fix().use { service ->
 *   // use service
 *   // <...>
 *
 *   IO.unit
 * }.fix()
 * //sampleEnd
 *
 * fun main() {
 *   managedTProgram.unsafeRunSync()
 * }
 * ```
 *
 * All three programs do exactly the same with varying levels of simplicity and overhead. `Resource` uses `Bracket` under the hood but provides a nicer monadic interface for creating and releasing resources in order, whereas bracket is great for one-off acquisitions but becomes more complex with nested resources.
 *
 **/
sealed class Resource<F, E, A> : ResourceOf<F, E, A> {

  /**
   * Use the created resource
   * When done will run all finalizers
   *
   * ```kotlin:ank:playground
   * import arrow.fx.IO
   * import arrow.fx.Resource
   * import arrow.fx.extensions.io.bracket.bracket
   * import arrow.fx.fix
   *
   * fun acquireResource(): IO<Int> = IO { println("Getting expensive resource"); 42 }
   * fun releaseResource(r: Int): IO<Unit> = IO { println("Releasing expensive resource: $r") }
   *
   * fun main() {
   *   //sampleStart
   *   val program = Resource(::acquireResource, ::releaseResource, IO.bracket()).use {
   *     IO { println("Expensive resource under use! $it") }
   *   }
   *   //sampleEnd
   *   program.fix().unsafeRunSync()
   * }
   * ```
   */
  fun <B> use(f: (A) -> Kind<F, B>): Kind<F, B> =
    fold(f, ::identity)

  @Deprecated("Api is being renamed to `use` for explicitness", ReplaceWith("use(use)"))
  operator fun <C> invoke(use: (A) -> Kind<F, C>): Kind<F, C> =
    use(use)

  fun <B> map(BR: Bracket<F, E>, f: (A) -> B): Resource<F, E, B> =
    flatMap { just(f(it), BR) }

  fun <B> ap(BR: Bracket<F, E>, ff: ResourceOf<F, E, (A) -> B>): Resource<F, E, B> =
    flatMap { res -> ff.fix().map(BR) { it(res) } }

  fun <B> flatMap(f: (A) -> ResourceOf<F, E, B>): Resource<F, E, B> =
    Bind(this, f)

  fun combine(other: ResourceOf<F, E, A>, SR: Semigroup<A>, BR: Bracket<F, E>): Resource<F, E, A> =
    flatMap { r ->
      other.fix().map(BR) { r2 -> SR.run { r.combine(r2) } }
    }

  internal class Bind<F, E, A, B>(val source: Resource<F, E, A>, val f: (A) -> ResourceOf<F, E, B>) : Resource<F, E, B>()

  internal class Allocate<F, E, A>(
    val acquire: () -> Kind<F, A>,
    val release: (A, ExitCase<E>) -> Kind<F, Unit>,
    val BR: Bracket<F, E>
  ) : Resource<F, E, A>()

  internal class Suspend<F, E, A>(val resource: Kind<F, Resource<F, E, A>>, val BR: Bracket<F, E>) : Resource<F, E, A>()

  private fun <B> fold(
    onOutput: (A) -> Kind<F, B>,
    onRelease: (Kind<F, Unit>) -> Kind<F, Unit>
  ): Kind<F, B> {
    // Interpreter that knows how to evaluate a Resource data structure
    // Maintains its own stack for dealing with Bind chains
    tailrec fun loop(current: Resource<F, E, A>, stack: List<(A) -> Resource<F, E, A>>): Kind<F, B> =
      when (current) {
        is Suspend -> current.BR.run { current.resource.flatMap { loop(it, stack) } }
        is Bind<*, *, *, *> ->
          loop(current.source as Resource<F, E, A>, listOf(current.f as (A) -> Resource<F, E, A>) + stack)
        is Allocate ->
          current.BR.run {
            current.acquire().bracketCase(
              { a, exitCase -> onRelease(current.release(a, exitCase)) },
              { a ->
                when {
                  stack.isEmpty() -> onOutput(a)
                  else -> loop(stack.first()(a), stack.drop(1))
                }
              })
          }
      }

    return loop(this, emptyList())
  }

  companion object {
    /**
     * Lift a value in context [F] into a [Resource]. Use with caution as the value will have no finalizers added.
     */
    fun <F, E, A> Kind<F, A>.liftF(BR: Bracket<F, E>): Resource<F, E, A> =
      Suspend(BR.run { this@liftF.map { just(it, BR) } }, BR)

    /**
     * [Monoid] empty. Creates an empty [Resource] using a given [Monoid] for [A]. Use with caution as the value will have no finalizers added.
     */
    fun <F, E, A> empty(MR: Monoid<A>, BR: Bracket<F, E>): Resource<F, E, A> =
      just(MR.empty(), BR)

    /**
     * Create a [Resource] from a value [A]. Use with caution as the value will have no finalizers added.
     */
    fun <F, E, A> just(r: A, BR: Bracket<F, E>): Resource<F, E, A> =
      Resource({ BR.just(r) }, { _, _ -> BR.unit() }, BR)

    fun <F, E, A, B> tailRecM(BR: Bracket<F, E>, a: A, f: (A) -> ResourceOf<F, E, Either<A, B>>): Resource<F, E, B> {
      fun loop(r: Resource<F, E, Either<A, B>>): Resource<F, E, B> = when (r) {
        is Bind<*, *, *, *> -> Bind(r.source as Resource<F, E, A>, (r.f as (A) -> ResourceOf<F, E, Either<A, B>>).andThen { loop(it.fix()) })
        is Allocate -> {
          Suspend(
            BR.run {
              r.acquire().flatMap { res ->
                when (res) {
                  is Either.Left -> r.release(res, ExitCase.Completed).map { tailRecM(BR, res.a, f) }
                  is Either.Right -> just(Allocate({ just(res.b) }, { _, ec -> r.release(res, ec) }, BR))
                }
              }
            },
            BR
          )
        }
        is Suspend -> Suspend(r.BR.run { r.resource.map(::loop) }, r.BR)
      }

      return loop(f(a).fix())
    }

    /**
     * Construct a [Resource] from a allocating function [acquire] and a release function [release].
     *
     * ```kotlin:ank:playground
     * import arrow.fx.IO
     * import arrow.fx.Resource
     * import arrow.fx.extensions.io.bracket.bracket
     * import arrow.fx.fix
     *
     * fun acquireResource(): IO<Int> = IO { println("Getting expensive resource"); 42 }
     * fun releaseResource(r: Int): IO<Unit> = IO { println("Releasing expensive resource: $r") }
     *
     * fun main() {
     *   //sampleStart
     *   val resource = Resource(::acquireResource, ::releaseResource, IO.bracket())
     *   //sampleEnd
     *   resource.use {
     *     IO { println("Expensive resource under use! $it") }
     *   }.fix().unsafeRunSync()
     * }
     * ```
     */
    operator fun <F, E, A> invoke(
      acquire: () -> Kind<F, A>,
      release: (A, ExitCase<E>) -> Kind<F, Unit>,
      BR: Bracket<F, E>
    ): Resource<F, E, A> = Allocate(acquire, release, BR)

    /**
     * Construct a [Resource] from a allocating function [acquire] and a release function [release].
     *
     * @see [use] For a version that provides an [ExitCase] to [release]
     */
    operator fun <F, E, A> invoke(
      acquire: () -> Kind<F, A>,
      release: (A) -> Kind<F, Unit>,
      BR: Bracket<F, E>
    ): Resource<F, E, A> = invoke(acquire, { r, _ -> release(r) }, BR)
  }
}
