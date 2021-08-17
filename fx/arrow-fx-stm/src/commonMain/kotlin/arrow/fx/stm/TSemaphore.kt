package arrow.fx.stm

public fun STM.newTSem(initial: Int): TSemaphore = TSemaphore(newTVar(checkNotNegative(initial)))

/**
 * [TSemaphore] is the transactional Semaphore.
 *
 * Semaphores are mostly used to limit concurrent access to resources by how many permits it can give out.
 *
 * ## Creating a [TSemaphore]
 *
 * A [TSemaphore] is created by using either [TSemaphore.new] outside of transactions or [STM.newTSem] inside a transaction.
 * Both of these methods throw if the supplied initial value is negative.
 *
 * ## Acquiring one or more permits
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TSemaphore
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tsem = TSemaphore.new(5)
 *   atomically {
 *     // acquire one permit
 *     tsem.acquire()
 *     // acquire 3 permits
 *     tsem.acquire(3)
 *   }
 *   //sampleEnd
 *   println("Permits remaining ${atomically { tsem.available() }}")
 * }
 * ```
 *
 * Should there be not enough permits the transaction will retry and wait until there are enough permits available again.
 *  [STM.tryAcquire] can be used to avoid this behaviour as it returns whether or not acquisition was successful.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TSemaphore
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tsem = TSemaphore.new(0)
 *   val result = atomically {
 *     tsem.tryAcquire()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("Permits remaining ${atomically { tsem.available() }}")
 * }
 * ```
 *
 * ## Release permits after use:
 *
 * Permits can be released again using [STM.release]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TSemaphore
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tsem = TSemaphore.new(5)
 *   atomically {
 *     tsem.release()
 *   }
 *   //sampleEnd
 *   println("Permits remaining ${atomically { tsem.available() }}")
 * }
 * ```
 *
 * > As you can see there is no upper limit enforced when releasing. You are free to release more or less permits than you have taken, but that may
 *  invalidate some other implicit rules so doing so is not advised.
 *
 * > [STM.release] will throw if given a negative number of permits.
 *
 * ## Reading how many permits are currently available
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TSemaphore
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tsem = TSemaphore.new(5)
 *   val result = atomically {
 *     tsem.available()
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("Permits remaining ${atomically { tsem.available() }}")
 * }
 * ```
 *
 */
public data class TSemaphore internal constructor(internal val v: TVar<Int>) {
  public companion object {
    public suspend fun new(initial: Int): TSemaphore = TSemaphore(TVar.new(checkNotNegative(initial)))
  }
}

private fun checkNotNegative(n: Int): Int = if (n < 0) throw IllegalArgumentException("n must be non-negative") else n
