package arrow.fx.coroutines

import arrow.core.Either

/**
 * A counting [Semaphore] has a non-negative number of permits available.
 * It's used to track how many permits are in-use,
 * and to automatically await a number of permits to become available.
 *
 * Acquiring permits decreases the available permits, and releasing increases the available permits
 *
 * Acquiring permits when there aren't enough available will suspend the acquire call
 * until the requested become available. Note that acquires are satisfied in strict FIFO order.
 * The suspending acquire calls are cancellable, and will release any already acquired permits.
 *
 * Let's say we want to guarantee mutually exclusiveness, we can use a `Semaphore` with a single permit.
 * Having a `Semaphore` with a single permit, we can track that only a single context can access something.
 *
 * ```kotlin:ank:playground
 * //sampleStart
 * import arrow.fx.coroutines.*
 * import java.util.concurrent.atomic.AtomicInteger
 *
 * /* Only allwos single accesor */
 * class PreciousFile(private val accesors: AtomicInteger = AtomicInteger(0)) {
 *     fun use(): Unit {
 *        check(accesors.incrementAndGet() == 1) { "File accessed before released" }
 *        check(accesors.decrementAndGet() == 0) { "File accessed before released" }
 *     }
 * }
 *
 * suspend fun main() {
 *   val file = PreciousFile()
 *   val mutex = Semaphore(1)
 *
 *   (0 until 100).parTraverse(IOPool) { i ->
 *     mutex.withPermit {
 *       val res = file.use()
 *       println("$i accessed PreciousFile on ${Thread.currentThread().name}")
 *     }
 *   }
 * //sampleEnd
 * }
 * ```
 *
 * By wrapping our operation in `withPermit` we ensure that our `var count: Int` is only updated by a single thread at the same time.
 * If we wouldn't protect our `PreciousFile` from being access by only a single thread at the same time, then it'll blow up our program.
 *
 * This is a common use-case when you need to write to a single `File` from different threads, since concurrent writes could result in inconsistent state.
 *
 * `Semaphore` is more powerful besides just modelling mutally exlusiveness,
 * since it's allows to track any amount of permits.
 * You can also use it to limit amount of parallel tasks, for example when using `parTraverse` we might want to limit how many tasks are running effectively in parallel.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun heavyProcess(i: Int): Unit {
 *   println("Started job $i")
 *   sleep(250.milliseconds)
 *   println("Finished job $i")
 * }
 *
 * suspend fun main(): Unit {
 *  val limit = 3
 *  val semaphore = Semaphore(3)
 *  (0..50).parTraverse { i ->
 *    semaphore.withPermit { heavyProcess(i) }
 *  }
 * }
 * ```
 *
 * Here we set a limit of `3` to ensure that only 3 `heavyProcess` are running at the same time.
 * This can ensure we don't stress the JVM too hard, OOM or worse.
 */
interface Semaphore {

  /**
   * Get a snapshot of the currently available permits, always non negative.
   *
   * May be out of data instantly, use [tryAcquire] or [tryAcquireN]
   * for acquires that immediately return if failed.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val semaphore = Semaphore(5)
   *   val available = semaphore.available()
   *   //sampleEnd
   *   println("available: $available")
   * }
   **/
  suspend fun available(): Long

  /**
   * Get a snapshot of the number of permits callers are waiting for when there are no permits available.
   *
   * The count is current available permits minus the outstanding acquires.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val semaphore = Semaphore(5)
   *   val count = semaphore.count()
   *   //sampleEnd
   *   println("$count: count")
   * }
   **/
  suspend fun count(): Long

  /**
   * Acquire [n] permits, suspends until the required permits are available.
   * When it gets cancelled while suspending it will release its already acquired permits.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val semaphore = Semaphore(5)
   *
   *   timeOutOrNull(2.seconds) {
   *     semaphore.acquireN(10) // count: -5
   *   }.also { println("I timed out with: $it") }
   *
   *   semaphore.acquireN(5)
   *   val available = semaphore.available()
   *   //sampleEnd
   *   println(available)
   * }
   * ```
   *
   * @param n number of permits to acquire - must be >= 0
   */
  suspend fun acquireN(n: Long): Unit

  /**
   * Acquire 1 permit, suspends until the requested permit is available.
   *
   * @see acquireN
   */
  suspend fun acquire(): Unit = acquireN(1)

  /**
   * Acquires [n] permits and signals success with a [Boolean] immediately.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val semaphore = Semaphore(5)
   *   val failed = semaphore.tryAcquireN(6)
   *   val succeed = semaphore.tryAcquireN(5)
   *   //sampleEnd
   *   println("failed: $failed, succeed: $succeed")
   * }
   * ```
   *
   * @param n number of permits to acquire - must be >= 0
   */
  suspend fun tryAcquireN(n: Long): Boolean

  /**
   * Acquire 1 permit and signals success with a [Boolean] immediately.
   *
   * @see acquireN
   */
  suspend fun tryAcquire(): Boolean = tryAcquireN(1)

  /**
   * Releases [n] permits, potentially unblocking outstanding acquires.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val semaphore = Semaphore(5)
   *   semaphore.acquireN(5)
   *   semaphore.releaseN(3)
   *   val available = semaphore.available()
   *   //sampleEnd
   *   println("available: available")
   * }
   * ```
   *
   * @param n number of permits to release - must be >= 0
   */
  suspend fun releaseN(n: Long): Unit

  /**
   * Releases 1 permit, potentially unblocking an outstanding acquire for 1 permit.
   *
   * @see acquireN
   */
  suspend fun release(): Unit = releaseN(1)

  /**
   * Returns an effect that acquires a permit, runs the supplied effect, and then releases the permit.
   */
  /**
   * Runs the supplied effect with an acquired permit, and releases the permit on [ExitCase].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val semaphore = Semaphore(5)
   *   val available = semaphore.withPermitN(4) {
   *     println("I'll run after I got 4 permits first")
   *     semaphore.available()
   *   }
   *   //sampleEnd
   *   println("available: $available")
   * }
   * ```
   */
  suspend fun <A> withPermitN(n: Long, fa: suspend () -> A): A

  suspend fun <A> withPermit(fa: suspend () -> A): A =
    withPermitN(1, fa)

  companion object {

    /**
     * Construct a [Semaphore] initialized with [n] available permits.
     *
     * ```kotlin:ank:playground
     * import arrow.fx.coroutines.*
     *
     * suspend fun main(): Unit {
     *   //sampleStart
     *   val semaphore = Semaphore(5)
     *   //sampleEnd
     * }
     */
    suspend operator fun invoke(n: Long): Semaphore =
      unsafe(n)

    suspend operator fun invoke(n: Int): Semaphore =
      unsafe(n.toLong())

    fun unsafe(n: Long): Semaphore {
      val r = Atomic.unsafe<SemaphoreState>(Either.Right(n))
      return SemaphoreDefault(r)
    }
  }
}

/**
 * A Semaphore `Either` has `acquireN` permits waiting,
 * or has permits available.
 */
internal typealias SemaphoreState = Either<AcquiredPermits, Long>
internal typealias AcquiredPermits = IQueue<Pair<Long, Promise<Unit>>>

internal fun assertNonNegative(n: Long): Unit =
  if (n < 0) throw IllegalArgumentException("n must be non-negative, was: $n")
  else Unit

private class SemaphoreDefault(private val state: Atomic<SemaphoreState>) : Semaphore {

  suspend fun mkGate(): Promise<Unit> = Promise()

  private suspend fun open(gate: Promise<Unit>): Unit {
    gate.complete(Unit)
  }

  override suspend fun count(): Long =
    when (val curr = state.get()) {
      is Either.Left -> -curr.a.map(Pair<Long, Promise<Unit>>::first).sum()
      is Either.Right -> curr.b
    }

  override suspend fun acquireN(n: Long): Unit =
    bracketCase(
      acquire = { acquireNInternal(n) },
      use = { (g, _) -> g.invoke() },
      release = { (_, c), ex ->
        when (ex) {
          ExitCase.Cancelled -> c.invoke()
          else -> Unit
        }
      }
    )

  suspend fun acquireNInternal(n: Long): Pair<suspend () -> Unit, suspend () -> Unit> {
    assertNonNegative(n)
    return if (n == 0L) Pair(suspend { Unit }, suspend { Unit })
    else {
      val p = mkGate()
      val newState = state.updateAndGet { old ->
        val update = when (old) {
          is Either.Left -> Either.Left(old.a.enqueue(Pair(n, p)))
          is Either.Right -> {
            if (n <= old.b) Either.Right(old.b - n)
            else Either.Left(IQueue(Pair(n - old.b, p)))
          }
        }
        update
      }

      when (newState) {
        is Either.Left -> {
          val cleanup: suspend () -> Unit = suspend {
            state.modify { s ->
              val update = when (s) {
                is Either.Left -> {
                  val permitsToRelease = s.a.find { it.second == p }?.first
                  permitsToRelease?.let { m ->
                    Pair(Either.Left(s.a.filterNot { it.second == p }), suspend { releaseN(n - m) })
                  } ?: Pair(Either.Left(s.a), suspend { releaseN(n) })
                }
                is Either.Right -> Pair(Either.Right(s.b + n), suspend { Unit })
              }

              update
            }.invoke()
          }

          val entry = newState.a.lastOrNull()
            ?: throw IllegalStateException("Semaphore has empty waiting queue rather than 0 count")

          Pair(entry.second::get, cleanup)
        }

        is Either.Right -> Pair(suspend { Unit }, suspend { releaseN(n) })
      }
    }
  }

  override suspend fun tryAcquireN(n: Long): Boolean {
    assertNonNegative(n)
    return if (n == 0L) true
    else {
      val (previous, now) = state.modify { old ->
        val update = when {
          old is Either.Right && old.b >= n -> Either.Right(old.b - n)
          else -> old
        }

        Pair(update, Pair(old, update))
      }
      cancelBoundary()
      when (now) {
        is Either.Left -> false
        is Either.Right -> when (previous) {
          is Either.Left -> false
          is Either.Right -> now.b != previous.b
        }
      }
    }
  }

  // Calculate new state when to hand out permits.
  private tailrec fun calculateNewState(m: Long, permits: AcquiredPermits): SemaphoreState =
    if (permits.isNotEmpty() && m > 0) {
      val (k, gate) = permits.first()
      if (k > m) calculateNewState(0, Pair(k - m, gate) prependTo permits.drop(1))
      else calculateNewState(m - k, permits.drop(1))
    } else {
      if (permits.isNotEmpty()) Either.Left(permits)
      else Either.Right(m)
    }

  override suspend fun releaseN(n: Long): Unit {
    assertNonNegative(n)
    return if (n == 0L) Unit
    else {
      val (prev, new) = state.modify { old ->
        val update = old.fold({ waiting ->
          calculateNewState(n, waiting)
        }, { m -> // Nobody waiting for permits
          Either.Right(m + n)
        })

        Pair(update, Pair(old, update))
      }

      when (prev) {
        is Either.Left -> {
          val waiting = prev.a

          val newSize = when (new) {
            is Either.Left -> new.a.size
            is Either.Right -> 0
          }

          val released = waiting.size - newSize
          waiting.take(released).foldRight(Unit) { (_, gate), _ ->
            open(gate)
          }
        }
        is Either.Right -> Unit
      }
    }
  }

  override suspend fun available(): Long =
    when (val curr = state.get()) {
      is Either.Left -> 0
      is Either.Right -> curr.b
    }

  override suspend fun <A> withPermitN(n: Long, fa: suspend () -> A): A =
    bracketCase(
      acquire = { acquireNInternal(n) },
      use = { (g, _) ->
        g.invoke()
        fa.invoke()
      },
      release = { (_, c), _ -> c.invoke() }
    )
}
