package arrow.effects

import arrow.Kind
import arrow.core.andThen
import arrow.documented
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.ExitCase
import arrow.higherkind
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

/** *
 * [Resource] models resource allocation and releasing. It is especially useful when multiple resources that depend on each other
 *  need to be acquired and later released in reverse order.
 * When a resource is created one can make use of [invoke] to run a computation with the resource. The finalizers are then
 *  guaranteed to run afterwards in reverse order of acquisition.
 **/
@higherkind
interface Resource<F, E, A> : ResourceOf<F, E, A> {

  val BR: Bracket<F, E>

  /**
   * Use the created resource
   * When done will run all finalizers
   *
   * ```kotlin:ank:playground
   * import arrow.effects.IO
   * import arrow.effects.Resource
   * import arrow.effects.extensions.io.bracket.bracket
   * import arrow.effects.fix
   *
   * fun acquireResource(): IO<Int> = IO { println("Getting expensive resource"); 42 }
   * fun releaseResource(r: Int): IO<Unit> = IO { println("Releasing expensive resource: $r") }
   *
   * fun main() {
   *   //sampleStart
   *   val program = Resource(::acquireResource, ::releaseResource, IO.bracket()).invoke {
   *     IO { println("Expensive resource under use! $it") }
   *   }
   *   //sampleEnd
   *   program.fix().unsafeRunSync()
   * }
   * ```
   */
  operator fun <C> invoke(use: (A) -> Kind<F, C>): Kind<F, C>

  fun <B> map(f: (A) -> B): Resource<F, E, B> = flatMap(f andThen { just(it, BR) })

  fun <B> ap(ff: Resource<F, E, (A) -> B>): Resource<F, E, B> = flatMap { res -> ff.map { it(res) } }

  fun <B> flatMap(f: (A) -> Resource<F, E, B>): Resource<F, E, B> = object : Resource<F, E, B> {
    override fun <C> invoke(use: (B) -> Kind<F, C>): Kind<F, C> = this@Resource { a ->
      f(a).invoke { b ->
        use(b)
      }
    }

    override val BR: Bracket<F, E> = this@Resource.BR
  }

  fun combine(other: Resource<F, E, A>, SR: Semigroup<A>): Resource<F, E, A> = flatMap { r ->
    other.map { r2 -> SR.run { r.combine(r2) } }
  }

  companion object {
    /**
     * Lift a value in context [F] into a [Resource]. Use with caution as the value will have no finalizers added.
     */
    fun <F, E, A> Kind<F, A>.liftF(BR: Bracket<F, E>): Resource<F, E, A> = Resource({ this }, { _, _ -> BR.unit() }, BR)

    /**
     * [Monoid] empty. Creates an empty [Resource] using a given [Monoid] for [A]. Use with caution as the value will have no finalizers added.
     */
    fun <F, E, A> empty(MR: Monoid<A>, BR: Bracket<F, E>): Resource<F, E, A> = just(MR.empty(), BR)

    /**
     * Create a [Resource] from a value [A]. Use with caution as the value will have no finalizers added.
     */
    fun <F, E, A> just(r: A, BR: Bracket<F, E>): Resource<F, E, A> = Resource({ BR.just(r) }, { _, _ -> BR.just(Unit) }, BR)

    /**
     * Construct a [Resource] from a allocating function [acquire] and a release function [release].
     *
     * ```kotlin:ank:playground
     * import arrow.effects.IO
     * import arrow.effects.Resource
     * import arrow.effects.extensions.io.bracket.bracket
     * import arrow.effects.fix
     *
     * fun acquireResource(): IO<Int> = IO { println("Getting expensive resource"); 42 }
     * fun releaseResource(r: Int): IO<Unit> = IO { println("Releasing expensive resource: $r") }
     *
     * fun main() {
     *   //sampleStart
     *   val resource = Resource(::acquireResource, ::releaseResource, IO.bracket())
     *   //sampleEnd
     *   resource.invoke {
     *     IO { println("Expensive resource under use! $it") }
     *   }.fix().unsafeRunSync()
     * }
     * ```
     */
    operator fun <F, E, A> invoke(
      acquire: () -> Kind<F, A>,
      release: (A, ExitCase<E>) -> Kind<F, Unit>,
      BR: Bracket<F, E>
    ): Resource<F, E, A> = object : Resource<F, E, A> {
      override operator fun <C> invoke(use: (A) -> Kind<F, C>): Kind<F, C> =
        BR.run { acquire().bracketCase(release, use) }

      override val BR: Bracket<F, E> = BR
    }

    /**
     * Construct a [Resource] from a allocating function [acquire] and a release function [release].
     *
     * @see [invoke] For a version that provides an [ExitCase] to [release]
     */
    operator fun <F, E, A> invoke(
      acquire: () -> Kind<F, A>,
      release: (A) -> Kind<F, Unit>,
      BR: Bracket<F, E>
    ): Resource<F, E, A> = invoke(acquire, { r, _ -> release(r) }, BR)
  }
}