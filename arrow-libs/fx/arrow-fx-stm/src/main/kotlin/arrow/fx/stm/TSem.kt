package arrow.fx.stm

import arrow.fx.coroutines.Semaphore

fun STM.newTSem(initial: Int): TSem = TSem(newTVar(checkNotNegative(initial)))

/**
 * [TSem] is the transactional analog to [Semaphore].
 *
 * Semaphores are mostly used to limit concurrent access to resources and have to major operations:
 * - [STM.acquire] to acquire 1 or n permits
 * - [STM.release] to release 1 or n permits
 *
 * ## Creating a [TSem]
 *
 * A [TSem] is created by using either [TSem.new] outside of transactions or [STM.newTSem] inside a transaction.
 * Both of these methods throw if the supplied initial value is negative.
 */
data class TSem internal constructor(internal val v: TVar<Int>) {
  companion object {
    suspend fun new(initial: Int): TSem = TSem(TVar.new(checkNotNegative(initial)))
  }
}

private fun checkNotNegative(n: Int): Int = if (n < 0) throw IllegalArgumentException("n must be non-negative") else n
