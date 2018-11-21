package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.typeclasses.Async
import arrow.typeclasses.ApplicativeError

/**
 * A counting [Semaphore] is used to control access to a resource in a concurrent system.
 * It keeps track of the count of available resources.
 */
interface Semaphore<F> {

  /**
   * Get a snapshot of the currently available permits, always non negative/
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
   *
   *   val result = semaphore.flatMap { s ->
   *     s.available
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   **/
  val available: Kind<F, Long>

  /**
   * Get a snapshot of the current count, may be negative.
   * The count is current available permits minus the outstanding acquires.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
   *
   *   val result = semaphore.flatMap { s ->
   *     s.count
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   **/
  val count: Kind<F, Long>

  /**
   * Acquires [n] resources
   * Suspending the fiber running the action until the resources are available.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
   *
   *   semaphore.flatMap { s ->
   *     s.acquireN(6)
   *   }.unsafeRunTimed(3.seconds) == IO.never().unsafeRunTimed(3.seconds)
   *
   *   semaphore.flatMap { s ->
   *     s.acquireN(5).flatMap. {
   *       s.available
   *     }
   *   }.unsafeRunSync() == 0
   *   //sampleEnd
   * }
   * ```
   */
  fun acquireN(n: Long): Kind<F, Unit>

  /**
   * Acquire a resource
   * Suspending the fiber running the action until the resource is available.
   *
   * @see acquireN
   */
  val acquire: Kind<F, Unit>
    get() = acquireN(1)

  /**
   * Try to acquires [n] resources and get an immediate response as [Boolean].
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
   *
   *   semaphore.flatMap { s ->
   *     s.tryAcquireN(6)
   *   }.unsafeRunSync() == false
   *
   *   semaphore.flatMap { s ->
   *     s.tryAcquireN(5)
   *   }.unsafeRunSync() == true
   *   //sampleEnd
   * }
   * ```
   */
  fun tryAcquireN(n: Long): Kind<F, Boolean>

  /**
   * Try to acquire a resource  and get an immediate response as [Boolean].
   *
   * @see tryAcquireN
   */
  val tryAcquire: Kind<F, Boolean>
    get() = tryAcquireN(1)

  /**
   * Release [n] resources
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
   *
   *   semaphore.flatMap { s ->
   *     s.acquireN(5).flatMap {
   *       s.releaseN(3).flatMap {
   *         s.available
   *       }
   *     }
   *   }.unsafeRunSync() == 3L
   *   //sampleEnd
   * }
   * ```
   */
  fun releaseN(n: Long): Kind<F, Unit>

  /**
   * Release a resources
   *
   * @see releaseN
   */
  val release: Kind<F, Unit>
    get() = releaseN(1)

  /**
   * Runs the supplied effect that acquires a permit, and then releases the permit.
   *
   * {: data-executable='true'}
   *
   * ```kotlin:ank
   * import arrow.effects.*
   * import arrow.effects.instances.io.async.async
   * import arrow.effects.instances.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
   *
   *   val result = semaphore.flatMap { s ->
   *     s.withPermit(IO { "Use controlled resource" })
   *   }.unsafeRunSync()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> withPermit(t: Kind<F, A>): Kind<F, A>

  companion object {

    /**
     * Construct a [Semaphore] initialized with [n] available permits.
     * Since it's based on [Async] it's constrained with an uncancelable [acquire] operation.
     *
     * {: data-executable='true'}
     *
     * ```kotlin:ank
     * import arrow.effects.*
     * import arrow.effects.instances.io.async.async
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
     *   //sampleEnd
     * }
     */
    fun <F> uncancelable(n: Long, AS: Async<F>): Kind<F, Semaphore<F>> = AS.run {
      assertNonNegative(n, AS).flatMap {
        Ref.of<F, State<F>>(Right(n), AS).map { ref -> AsyncSemaphore(ref, AS) }
      }
    }

  }

}

// A semaphore is either empty, and there are number of outstanding acquires (Left)
// or it is non-empty, and there are n permits available (Right)
private typealias State<F> = Either<List<Tuple2<Long, Promise<F, Unit>>>, Long>

private fun <F> assertNonNegative(n: Long, AE: ApplicativeError<F, Throwable>): Kind<F, Unit> =
  if (n < 0) AE.raiseError(IllegalArgumentException("n must be nonnegative, was: $n")) else AE.just(Unit)

internal class AsyncSemaphore<F>(private val state: Ref<F, State<F>>,
                                 private val AS: Async<F>) : Semaphore<F>, Async<F> by AS {

  override val available: Kind<F, Long>
    get() = state.get.map { state ->
      state.fold({ 0L }, ::identity)
    }

  override val count: Kind<F, Long>
    get() = state.get.map { state ->
      state.fold({ it.map { (a, _) -> a }.sum().unaryMinus() }, ::identity)
    }

  override fun acquireN(n: Long): Kind<F, Unit> =
    assertNonNegative(n, AS).flatMap {
      if (n == 0L) just(Unit)
      else Promise.uncancelable<F, Unit>(AS).flatMap { gate ->
        state.modify { old ->
          val u = old.fold({ waiting ->
            Left(waiting + listOf(n toT gate))
          }, { m ->
            if (n <= m) Right(m - n)
            else Left(listOf((n - m) toT gate))
          })

          Tuple2(u, u)
        }.flatMap { u ->
          u.fold({ waiting ->
            waiting.lastOrNone()
              .map { (_, promise) -> promise.get }
              .getOrElse { raiseError(RuntimeException("Semaphore has empty waiting queue rather than 0 count")) }
          }, {
            just(Unit)
          })
        }
      }
    }

  override fun tryAcquireN(n: Long): Kind<F, Boolean> =
    assertNonNegative(n, AS).flatMap { _ ->
      if (n == 0L) AS.just(true)
      else state.modify { old ->
        val u = old.fold({ Left(it) }, { m ->
          if (m >= n) Right(m - n) else Right(m)
        })

        u toT Tuple2(old, u)
      }.map { (previous, now) ->
        now.fold({ false }, { n ->
          previous.fold({ false }, { m ->
            n != m
          })
        })
      }
    }

  override fun releaseN(n: Long): Kind<F, Unit> =
    assertNonNegative(n, AS).flatMap {
      if (n == 0L) just(Unit)
      else state.modify { old ->
        val u = old.fold({ waiting ->
          var m = n
          var waiting2 = waiting
          while (waiting2.isNotEmpty() && m > 0) {
            val (k, gate) = waiting2.first()
            if (k > m) {
              waiting2 = listOf(Tuple2(k - m, gate)) + waiting2.drop(1)
              m = 0
            } else {
              m -= k
              waiting2 = waiting2.drop(1)
            }
          }
          if (waiting2.isNotEmpty()) Left(waiting2)
          else Right(m)
        }, { m ->
          Right(m + n)
        })

        Tuple2(u, Tuple2(old, u))
      }.flatMap { (previous, now) ->
        previous.fold({ waiting ->
          val newSize = now.fold({ it.size }, { 0 })
          val released = waiting.size - newSize
          waiting.take(released).foldRight(just(Unit)) { hd, tl -> open(hd.b).flatMap { tl } }
        }, { just(Unit) })
      }
    }

  private fun open(gate: Promise<F, Unit>): Kind<F, Unit> = gate.complete(Unit)

  override fun <A> withPermit(t: Kind<F, A>): Kind<F, A> =
    acquire.bracket({ release }, { t })

}
