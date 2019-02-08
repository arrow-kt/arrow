package arrow.effects

import arrow.Kind
import arrow.core.Option
import arrow.effects.internal.CancelableMVar
import arrow.effects.internal.UncancelableMVar
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Concurrent

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
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
   * import arrow.effects.extensions.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.empty<Int>().flatMap { v ->
   *   v.isEmpty()
   * }.unsafeRunSync() == true
   *
   * mvar.of(10).flatMap { v ->
   *   v.isEmpty()
   * }.unsafeRunSync() == false
   * //sampleEnd
   * }
   *```
   */
  fun isEmpty(): Kind<F, Boolean>

  /**
   * Returns true if there no elements. Otherwise false.
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
   * import arrow.effects.extensions.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.of(10).flatMap { v ->
   *   v.isNotEmpty()
   * }.unsafeRunSync() == true
   *
   * mvar.empty<Int>().flatMap { v ->
   *   v.isNotEmpty()
   * }.unsafeRunSync() == false
   * //sampleEnd
   * }
   *```
   */
  fun isNotEmpty(): Kind<F, Boolean>

  /**
   * Puts [A] in the [MVar] if it is empty,
   * or blocks if full until the given value is next in line to be consumed by [take].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
   * import arrow.effects.extensions.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.empty<Int>().flatMap { v ->
   *   v.put(5).flatMap {
   *     v.take()
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
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
   * import arrow.effects.extensions.io.monad.flatMap
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
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
   * import arrow.effects.extensions.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.of(5).flatMap { v ->
   * v.take()
   * }.unsafeRunSync() == 5
   *
   * mvar.empty<Int>().flatMap { v ->
   * v.take()
   * } //Never ends
   * //sampleEnd
   * }
   *```
   */
  fun take(): Kind<F, A>

  /**
   * Try to take the value of [MVar], returns result as an [Option].
   *
   * ```kotlin:ank:playground
   * import arrow.core.*
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
   * import arrow.effects.extensions.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.of(5).flatMap { v ->
   *   v.tryTake()
   * }.unsafeRunSync() == Some(5)
   *
   * mvar.empty<Int>().flatMap { v ->
   *   v.tryTake()
   * }.unsafeRunSync() == None
   * //sampleEnd
   * }
   *```
   */
  fun tryTake(): Kind<F, Option<A>>

  /**
   * Tries reading the current value, or blocks until there is a value available.
   *
   * ```kotlin:ank:playground
   * import arrow.core.toT
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
   * import arrow.effects.extensions.io.monad.flatMap
   * import arrow.effects.extensions.io.monad.map
   * fun main(args: Array<String>) {
   * //sampleStart
   * val mvar = MVar(IO.async())
   *
   * mvar.of(5).flatMap { v ->
   *   v.read()
   * }.unsafeRunSync() == 5
   *
   * mvar.of(5).flatMap { v ->
   *   v.read().flatMap { value ->
   *     v.isNotEmpty().map { isNotEmpty ->
   *       value toT isNotEmpty
   *     }
   *   }
   * }.unsafeRunSync() == 5 toT true
   *
   * mvar.empty<Int>().flatMap { v ->
   *   v.read()
   * } //Never ends
   * //sampleEnd
   * }
   *```
   */
  fun read(): Kind<F, A>

  companion object {

    /**
     * Create an cancelable empty [MVar].
     *
     * ```kotlin:ank:playground
     * import arrow.effects.*
     * import arrow.effects.extensions.io.concurrent.concurrent
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val mvar: IOOf<MVar<ForIO, Int>> = MVar.empty(IO.concurrent())
     *   //sampleEnd
     * }
     * ```
     */
    fun <F, A> empty(CF: Concurrent<F>): Kind<F, MVar<F, A>> =
      CancelableMVar.empty(CF)

    /**
     * Create an uncancelable [MVar] that's initialized to an [initial] value.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.*
     * import arrow.effects.extensions.io.concurrent.concurrent
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val mvar: IOOf<MVar<ForIO, Int>> = MVar.cancelable(5, IO.concurrent())
     *   //sampleEnd
     * }
     * ```
     */
    fun <F, A> cancelable(initial: A, CF: Concurrent<F>): Kind<F, MVar<F, A>> =
      CancelableMVar(initial, CF)

    /**
     * Create an uncancelable [MVar] that's initialized to an [initial] value.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.*
     * import arrow.effects.extensions.io.concurrent.concurrent
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val mvar: IOOf<MVar<ForIO, Int>> = MVar.cancelable(5, IO.concurrent())
     *   //sampleEnd
     * }
     * ```
     */
    operator fun <F, A> invoke(initial: A, CF: Concurrent<F>): Kind<F, MVar<F, A>> =
      CancelableMVar(initial, CF)

    /**
     * Create an uncancelable empty [MVar].
     *
     * ```kotlin:ank:playground
     * import arrow.effects.*
     * import arrow.effects.extensions.io.async.async
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val mvar: IOOf<MVar<ForIO, Int>> = MVar.uncancelableEmpty(IO.async())
     *   //sampleEnd
     * }
     * ```
     */
    fun <F, A> uncancelableEmpty(AS: Async<F>): Kind<F, MVar<F, A>> =
      UncancelableMVar.empty(AS)

    /**
     * Create an uncancelable [MVar] that's initialized to an [initial] value.
     *
     * ```kotlin:ank:playground
     * import arrow.effects.*
     * import arrow.effects.extensions.io.async.async
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val mvar: IOOf<MVar<ForIO, Int>> = MVar.uncancelableOf(5, IO.async())
     *   //sampleEnd
     * }
     * ```
     */
    fun <F, A> uncancelableOf(initial: A, AS: Async<F>): Kind<F, MVar<F, A>> =
      UncancelableMVar(initial, AS)

    operator fun <F> invoke(AS: Async<F>) = object : MVarPartialOf<F> {

      override fun <A> of(a: A): Kind<F, MVar<F, A>> =
        UncancelableMVar(a, AS)

      override fun <A> empty(): Kind<F, MVar<F, A>> =
        UncancelableMVar.empty(AS)
    }

    operator fun <F> invoke(CF: Concurrent<F>) = object : MVarPartialOf<F> {

      override fun <A> of(a: A): Kind<F, MVar<F, A>> =
        CancelableMVar(a, CF)

      override fun <A> empty(): Kind<F, MVar<F, A>> =
        CancelableMVar.empty(CF)
    }

  }

}

/**
 * Builds a [MVar] value for data types [F]
 * without deciding the type of the MVar's value.
 *
 * ```kotlin:ank:playground
 * import arrow.effects.*
 * import arrow.effects.extensions.io.async.async
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
   * Builds a [MVar] with a value of type [A].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
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
   * Builds an empty [MVar] for type [A].
   *
   * ```kotlin:ank:playground
   * import arrow.effects.*
   * import arrow.effects.extensions.io.async.async
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
