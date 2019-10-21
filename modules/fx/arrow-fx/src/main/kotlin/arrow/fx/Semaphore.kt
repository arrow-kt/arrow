package arrow.fx

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.Tuple2
import arrow.core.getOrElse
import arrow.core.identity
import arrow.core.lastOrNone
import arrow.core.toT
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.Concurrent
import arrow.typeclasses.ApplicativeError

/**
 * A counting [Semaphore] is used to control access to a resource in a concurrent system.
 * It keeps track of the count of available resources.
 */
interface Semaphore<F> {

  /**
   * Get a snapshot of the currently available permits, always non negative.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.io.async.async
   * import arrow.fx.extensions.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
   *
   *   val result = semaphore.flatMap { s ->
   *     s.available()
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   **/
  fun available(): Kind<F, Long>

  /**
   * Get a snapshot of the current count, may be negative.
   * The count is current available permits minus the outstanding acquires.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.io.async.async
   * import arrow.fx.extensions.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
   *
   *   val result = semaphore.flatMap { s ->
   *     s.count()
   *   }
   *   //sampleEnd
   *   println(result)
   * }
   **/
  fun count(): Kind<F, Long>

  /**
   * Acquires [n] resources
   * Suspending the [Fiber] running the action until the resources are available.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.io.async.async
   * import arrow.fx.extensions.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
   *
   *   semaphore.flatMap { s ->
   *     s.acquireN(6)
   *   } //Never ends since is uncancelable
   *
   *   semaphore.flatMap { s ->
   *     s.acquireN(5).flatMap {
   *       s.available()
   *     }
   *   }.unsafeRunSync() == 0L
   *   //sampleEnd
   * }
   * ```
   */
  fun acquireN(n: Long): Kind<F, Unit>

  /**
   * Acquire a resource
   * Suspending the [Fiber] running the action until the resource is available.
   *
   * @see acquireN
   */
  fun acquire(): Kind<F, Unit> =
    acquireN(1)

  /**
   * Try to acquires [n] resources and get an immediate response as [Boolean].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.io.async.async
   * import arrow.fx.extensions.io.monad.flatMap
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
  fun tryAcquire(): Kind<F, Boolean> =
    tryAcquireN(1)

  /**
   * Release [n] resources
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.io.async.async
   * import arrow.fx.extensions.io.monad.flatMap
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
   *
   *   semaphore.flatMap { s ->
   *     s.acquireN(5).flatMap {
   *       s.releaseN(3).flatMap {
   *         s.available()
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
  fun release(): Kind<F, Unit> =
    releaseN(1)

  /**
   * Runs the supplied effect that acquires a permit, and then releases the permit.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.io.async.async
   * import arrow.fx.extensions.io.monad.flatMap
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
     *
     * ```kotlin:ank:playground
     * import arrow.fx.*
     * import arrow.fx.extensions.io.concurrent.concurrent
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val semaphore = Semaphore<ForIO>(5, IO.concurrent())
     *   //sampleEnd
     * }
     */
    operator fun <F> invoke(n: Long, CF: Concurrent<F>): Kind<F, Semaphore<F>> = CF.run {
      assertNonNegative(n).flatMap {
        Ref<F, State<F>>(CF, Right(n)).map { ref ->
          DefaultSemaphore(ref, Promise(this), this)
        }
      }
    }

    /**
     * Construct a [Semaphore] initialized with [n] available permits.
     * Since it's based on [Async] it's constrained with an uncancelable [acquire] operation.
     *
     * ```kotlin:ank:playground
     * import arrow.fx.*
     * import arrow.fx.extensions.io.async.async
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val semaphore = Semaphore.uncancelable<ForIO>(5, IO.async())
     *   //sampleEnd
     * }
     */
    fun <F> uncancelable(n: Long, AS: Async<F>): Kind<F, Semaphore<F>> = AS.run {
      assertNonNegative(n).flatMap {
        Ref<F, State<F>>(AS, Right(n)).map { ref ->
          DefaultSemaphore(ref, Promise.uncancelable(this), this)
        }
      }
    }
  }
}

// A semaphore is either empty, and there are number of outstanding acquires (Left)
// or it is non-empty, and there are n permits available (Right)
private typealias State<F> = Either<AcquiredPermits<F>, Long>

private typealias AcquiredPermits<F> = List<Tuple2<Long, Promise<F, Unit>>>

private fun <F> ApplicativeError<F, Throwable>.assertNonNegative(n: Long): Kind<F, Unit> =
  if (n < 0) raiseError(IllegalArgumentException("n must be nonnegative, was: $n")) else just(Unit)

internal class DefaultSemaphore<F>(
  private val state: Ref<F, State<F>>,
  private val promise: Kind<F, Promise<F, Unit>>,
  private val AS: Async<F>
) : Semaphore<F>, Async<F> by AS {

  override fun available(): Kind<F, Long> =
    state.get().map { state ->
      state.fold({ 0L }, ::identity)
    }

  override fun count(): Kind<F, Long> =
    state.get().map { state ->
      state.fold({ it.map { (a, _) -> a }.sum().unaryMinus() }, ::identity)
    }

  override fun acquireN(n: Long): Kind<F, Unit> =
    assertNonNegative(n).flatMap {
      if (n == 0L) just(Unit)
      else promise.flatMap { p ->
        state.modify { old ->
          val u = old.fold({ waiting ->
            Left(waiting + listOf(n toT p))
          }, { m ->
            if (n <= m) Right(m - n)
            else Left(listOf((n - m) toT p))
          })

          Tuple2(u, u)
        }.flatMap { u ->
          u.fold({ waiting ->
            waiting.lastOrNone()
              .map { (_, promise) -> promise.get() }
              .getOrElse { raiseError(RuntimeException("Semaphore has empty waiting queue rather than 0 count")) }
          }, {
            just(Unit)
          })
        }
      }
    }

  override fun tryAcquireN(n: Long): Kind<F, Boolean> =
    assertNonNegative(n).flatMap {
      if (n == 0L) just(true)
      else state.modify { old ->
        val u = old.fold({ waiting -> Left(waiting) }, { m ->
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
    assertNonNegative(n).flatMap {
      if (n == 0L) just(Unit)
      else state.modify { old ->
        val u = old.fold({ waiting: AcquiredPermits<F> ->
          tailrec fun loop(m: Long, waiting2: AcquiredPermits<F>): State<F> =
            if (waiting2.isNotEmpty() && m > 0) {
              val (k, gate) = waiting2.first()
              if (k > m) loop(0, listOf(Tuple2(k - m, gate)) + waiting2.drop(1))
              else loop(m - k, waiting2.drop(1))
            } else {
              if (waiting2.isNotEmpty()) Left(waiting2)
              else Right(m)
            }

          loop(n, waiting)
        }, { m ->
          Right(m + n)
        })

        Tuple2(u, Tuple2(old, u))
      }.flatMap { (previous, now) ->
        previous.fold({ waiting ->
          val newSize = now.fold({ newWaiting -> newWaiting.size }, { 0 })
          val released = waiting.size - newSize
          waiting.take(released).foldRight(just(Unit)) { (_, promise), tl -> promise.complete(Unit).flatMap { tl } }
        }, { just(Unit) })
      }
    }

  override fun <A> withPermit(t: Kind<F, A>): Kind<F, A> =
    acquire().bracket({ release() }, { t })

  override fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B> = AS.run {
    this@ap.ap(ff)
  }

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> = AS.run {
    this@map.map(f)
  }
}
