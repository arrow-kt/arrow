package arrow.effects

import arrow.Kind
import arrow.core.Option
import arrow.effects.internal.MVarAsync
import arrow.effects.typeclasses.Async

/**
 * It's a mutable variable with context [F], that is either empty or contains a value of type [A].
 * A pure concurrent queue of size 1.
 *
 * [MVar] is appropriate for building synchronization
 * primitives and performing simple inter-thread communications.
 */
interface MVar<F, A> {

  /**
   * Returns true if there are no elements. Otherwise false.
   *
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.empty<Int>().flatMap { v ->
   *   v.isEmpty
   * }.unsafeRunSync() == true
   *
   * mvar.of(10).flatMap { v ->
   *   v.isEmpty
   * }.unsafeRunSync() == false
   * //sampleEnd
   * }
   *```
   */
  val isEmpty: Kind<F, Boolean>

  /**
   * Returns true if there no elements. Otherwise false.
   *
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.of(10).flatMap { v ->
   *   v.isNotEmpty
   * }.unsafeRunSync() == true
   *
   * mvar.empty<Int>().flatMap { v ->
   *   v.isNotEmpty
   * }.unsafeRunSync() == false
   * //sampleEnd
   * }
   *```
   */
  val isNotEmpty: Kind<F, Boolean>

  /**
   * Puts [A] in the [MVar] if it is empty,
   * or blocks if full until the given value is next in line to be consumed by [take].
   *
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.empty<Int>().flatMap { v ->
   *   v.put(5).flatMap {
   *     v.take
   *   }
   * }.unsafeRunSync() == 5
   * //sampleEnd
   * }
   *```
   */
  fun put(a: A): Kind<F, Unit>

  /**
   * Fill the [MVar] if we can do it without blocking.
   * Returns true if successfully put the value or false otherwise.
   *
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.empty<Int>().flatMap { v ->
   *  v.tryPut(5)
   * }.unsafeRunSync() == true
   *
   * mvar.of(5).flatMap { v ->
   *   v.tryPut(10)
   * }.unsafeRunSync() == false
   * //sampleEnd
   * }
   *```
   */
  fun tryPut(a: A): Kind<F, Boolean>

  /**
   * Takes the value out of the [MVar] if full, or blocks until a value is available.
   *
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.of(5).flatMap { v ->
   * v.take
   * }.unsafeRunSync() == 5
   *
   * mvar.empty<Int>().flatMap { v ->
   * v.take
   * } //Never ends
   * //sampleEnd
   * }
   *```
   */
  val take: Kind<F, A>

  /**
   * Try to take the value of [MVar], returns result as an [Option].
   *
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.core.*
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.of(5).flatMap { v ->
   *   v.tryTake
   * }.unsafeRunSync() == Some(5)
   *
   * mvar.empty<Int>().flatMap { v ->
   *   v.tryTake
   * }.unsafeRunSync() == None
   * //sampleEnd
   * }
   *```
   */
  val tryTake: Kind<F, Option<A>>

  /**
   * Tries reading the current value, or blocks until there is a value available.
   *
   * {: data-executable='true'}
   * ```kotlin:ank
   * import arrow.core.toT
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   * import arrow.effects.instances.io.monad.map
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.of(5).flatMap { v ->
   *   v.read
   * }.unsafeRunSync() == 5
   *
   * mvar.of(5).flatMap { v ->
   *   v.read.flatMap { value ->
   *     v.isNotEmpty.map { isNotEmpty ->
   *       value toT isNotEmpty
   *     }
   *   }
   * }.unsafeRunSync() == 5 toT true
   *
   * mvar.empty<Int>().flatMap { v ->
   *   v.read
   * } //Never ends
   * //sampleEnd
   * }
   *```
   */
  val read: Kind<F, A>

  companion object {

    /**
     * Create an uncancelable empty [MVar].
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.effects.*
     * import arrow.effects.instances.io.async.async
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val mvar: IOOf<MVar<ForIO, Int>> = MVar.uncancelableEmpty(IO.async())
     *   //sampleEnd
     * }
     * ```
     */
    fun <F, A> uncancelableEmpty(AS: Async<F>): Kind<F, MVar<F, A>> =
      MVarAsync.empty(AS)

    /**
     * Create an uncancelable [MVar] that's initialized to an [initial] value.
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.effects.*
     * import arrow.effects.instances.io.async.async
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val mvar: IOOf<MVar<ForIO, Int>> = MVar.uncancelableOf(5, IO.async())
     *   //sampleEnd
     * }
     * ```
     */
    fun <F, A> uncancelableOf(initial: A, AS: Async<F>): Kind<F, MVar<F, A>> =
      MVarAsync(initial, AS)

    operator fun <F> invoke(AS: Async<F>) = object : MVarPartialOf<F> {

      override fun <A> of(a: A): Kind<F, MVar<F, A>> =
        MVarAsync(a, AS)

      override fun <A> empty(): Kind<F, MVar<F, A>> =
        MVarAsync.empty(AS)
    }

  }

}

/**
 * Builds a [Mvar] value for data types [F]
 * without deciding the type of the MVar's value.
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.effects.*
 * import arrow.effects.instances.io.async.async
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val mvarPartial: MVarPartialOf<ForIO> = MVar(IO.async())
 *   val intVar: IOOf<MVar<ForIO, Int>> = mvarPartial.of(5)
 *   val stringVar: IOOf<MVar<ForIO, String>> = mvarPartial.empty<String>()
 *   //sampleEnd
 * }
 * ```
 */
interface MVarPartialOf<F> {

  /**
   * Builds a [Mvar] with a value of type [A].
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val mvarPartial: MVarPartialOf<ForIO> = MVar(IO.async())
   *   val intVar: IOOf<MVar<ForIO, Int>> = mvarPartial.of(5)
   *   //sampleEnd
   * }
   * ```
   */
  fun <A> of(a: A): Kind<F, MVar<F, A>>

  /**
   * Builds an empty [Mvar] for type [A].
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val mvarPartial: MVarPartialOf<ForIO> = MVar(IO.async())
   *   val stringVar: IOOf<MVar<ForIO, String>> = mvarPartial.empty<String>()
   *   //sampleEnd
   * }
   * ```
   */
  fun <A> empty(): Kind<F, MVar<F, A>>

}
