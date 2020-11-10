package arrow.fx.stm

import arrow.fx.coroutines.AtomicRefW
import arrow.fx.coroutines.Duration
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.sleep
import arrow.fx.stm.internal.STMFrame
import arrow.fx.stm.internal.STMTransaction
import kotlinx.atomicfu.AtomicLong
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlin.coroutines.resume

/**
 * Utility to create [TVar] which sets its value to true after a [delay].
 */
suspend fun registerDelay(delay: Duration): TVar<Boolean> =
  TVar.new(false).also { v ->
    ForkConnected {
      sleep(delay)
      atomically { v.write(true) }
    }
  }

/**
 * A [TVar] is a mutable reference that can only be (safely) accessed inside a [STM] transaction.
 *
 * ## Creating a [TVar]
 *
 * There are two ways of creating [TVar]'s:
 * - [STM.newTVar] to create a [TVar] inside a transaction
 * - [TVar.new] to create a top-level [TVar] outside of a transaction
 *
 * Strictly speaking [TVar.new] is not necessary as it can be defined as `atomically { newTVar(v) }` however [TVar.new] is much
 *  faster because it avoids creating a (pointless) transaction.
 * [STM.newTVar] should be used inside transactions because it is not possible to use [TVar.new] inside [STM] due to `suspend`.
 *
 * ## Accessing and modifying a [TVar]
 *
 * Retrieving the value of a [TVar] can be done outside of transactions with [TVar.unsafeRead].
 * This is called unsafe because if the result is then used inside an [atomically] it may end up with an inconsistent state
 *  and thus a race-condition.
 * Using it for one-off reads is fine though and is also faster than `atomically { v.read() }` because it avoids creating a transaction.
 *
 * Inside a transaction several combinators are present to modify and read a [TVar]:
 * - [STM.read] Read the value from a [TVar]
 * - [STM.write] Write a new value to the [TVar]
 * - [STM.modify] Modify the [TVar] with a function. `modify(f) = write(f(read()))`
 * - [STM.swap] Swap the content of a [TVar]. `swap(a) = read().also { write(a) }`
 */
class TVar<A> internal constructor(a: A) {
  /**
   * The ref for a TVar stores either the STMFrame that currently locks the value or the value itself
   * This is used to implement locking. Reading threads have to loop until the value is released by a
   *  transaction.
   */
  private val ref: AtomicRef<Any?> = atomic(a as Any?)

  internal val value
    get() = ref.value

  /**
   * Each TVar has a unique id which is used to get a total ordering of variables to ensure that locks
   *  are always acquired in the same order on each thread
   *
   * > The current implementation no longer waits on locks which means lock order is irrelevant. This is still used as
   *  a good hash value though.
   */
  internal val id: Long = globalC.incrementAndGet()

  /**
   * A list of running transactions waiting for a change on this variable.
   * Changes are pushed to waiting transactions via [notify]
   */
  // TODO Use a set here, and preferably something that uses sharing to avoid gc pressure from copying...
  private val waiting = atomic<List<STMTransaction<*>>>(emptyList())

  override fun hashCode(): Int = id.hashCode()

  override fun equals(other: Any?): Boolean = this === other

  /**
   * Read the value of a [TVar]. This has no consistency guarantees for subsequent reads and writes
   *  since it is outside of a stm transaction.
   *
   * Much faster than `atomically { v.read() }` because it avoids creating a transaction, it just reads the value.
   */
  suspend fun unsafeRead(): A = this.readI()

  /**
   * Internal unsafe (non-suspend) version of read. Used by various other internals and [unsafeRead] to
   *  read the current value respecting its state.
   */
  internal fun readI(): A {
    while (true) {
      ref.value.let {
        if (it !is STMFrame) return@readI it as A
      }
    }
  }

  /**
   * Release a lock held by [frame].
   *
   * If [frame] no longer has the lock (a write happened and now read
   *  tries to unlock) it is ignored (By the semantics of [AtomicRefW.compareAndSet])
   */
  internal fun release(frame: STMFrame, a: A): Unit {
    ref.compareAndSet(frame, a as Any?)
  }

  /**
   * Lock a [TVar] by replacing the value with [frame].
   *
   * This forces all further reads to wait until [frame] is done with the value.
   *
   * This works by continuously calling [readI] and then trying to compare and set the frame.
   * If the value has been modified after reading it tries again, if the value inside is locked
   *  it will loop inside [readI] until it is unlocked.
   *
   * > This is unused atm because locks are only taken conditionally, but is kept because it helps testing and
   *  may be useful in the future.
   */
  internal fun lock(frame: STMFrame): A {
    var res: A
    do {
      res = this.readI()
    } while (ref.compareAndSet(res as Any?, frame).not())
    return res
  }

  /**
   * Lock a [TVar] by replacing the value with [frame] only if the current value is [expected]
   */
  internal fun lock_cond(frame: STMFrame, expected: A): Boolean =
    ref.compareAndSet(expected, frame)

  /**
   * Queue a transaction to be notified when this [TVar] is changed and [notify] is called.
   * This does not happen implicitly on [release] because release may also write the same value back on
   *  normal lock release.
   */
  internal fun registerWaiting(trans: STMTransaction<*>, expected: A): Boolean {
    if (value !== expected) {
      trans.getCont()?.resume(Unit)
      return false
    }
    waiting.update { it + trans }
    return if (value !== expected) {
      removeWaiting(trans)
      trans.getCont()?.resume(Unit)
      false
    } else true
  }

  /**
   * A transaction resumed so remove it from the [TVar]
   */
  internal fun removeWaiting(trans: STMTransaction<*>): Unit {
    waiting.update { it.filter { it !== trans } }
  }

  /**
   * Resume execution of all transactions waiting for this [TVar] to change.
   */
  internal fun notify(): Unit {
    waiting.getAndSet(emptyList()).forEach { it.getCont()?.resume(Unit) }
  }

  companion object {
    /**
     * Return a new [TVar]
     *
     * More efficient than `atomically { newVar(a) }` because it skips creating a transaction.
     */
    suspend fun <A> new(a: A): TVar<A> = TVar(a)
  }
}

internal val globalC: AtomicLong = atomic(0L)
