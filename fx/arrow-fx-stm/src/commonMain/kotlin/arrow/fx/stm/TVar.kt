package arrow.fx.stm

import arrow.fx.stm.internal.STMFrame
import arrow.fx.stm.internal.STMTransaction
import kotlinx.atomicfu.AtomicLong
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlin.coroutines.resume

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
 * ## Reading a value from a [TVar]
 *
 * One-off reading from a [TVar] outside of a transaction can be done by using [TVar.unsafeRead].
 * Despite the name using this method is only unsafe if the read value (or a derivative) is then used inside another transaction which may cause
 *  race conditions again. However the benefit of using this over `atomically { tvar.read() }` is that it avoids creating a transaction and is
 *  thus much faster.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TVar
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tvar = TVar.new(10)
 *   val result = tvar.unsafeRead()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * Reading from a [TVar] inside a transaction is done by using [STM.read].
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tvar = TVar.new(10)
 *   val result = atomically {
 *     tvar.read()
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * > Checking the validity of a transaction is done by checking the contents of all accessed [TVar]'s before locking the [TVar]'s that have
 *  been written to and then checking only the [TVar]'s that have only been read not modified again. To keep transactions as fast as possible
 *  it is key to keep the number of accessed [TVar]'s small.
 *
 * > Another important thing to remember is that only writes will ever lock a [TVar] and only those that need to be changed.
 *  This means that so long as transactions access disjoint sets of variables or a transaction is read only, they may run in parallel.
 *
 * ## Modifying the value inside the [TVar]
 *
 * Writing a new value to the [TVar]:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tvar = TVar.new(10)
 *   val result = atomically {
 *     tvar.write(20)
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * Modifying the value based on the initial value:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tvar = TVar.new(10)
 *   val result = atomically {
 *     tvar.modify { it * 2 }
 *   }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * Writing a new value to the [TVar] and returning the initial value:
 *
 * ```kotlin:ank:playground
 * import arrow.fx.stm.TVar
 * import arrow.fx.stm.atomically
 *
 * suspend fun main() {
 *   //sampleStart
 *   val tvar = TVar.new(10)
 *   val result = atomically {
 *     tvar.swap(20)
 *   }
 *   //sampleEnd
 *   println("Result $result")
 *   println("New value ${tvar.unsafeRead()}")
 * }
 * ```
 */
public class TVar<A> internal constructor(a: A) {
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
  public suspend fun unsafeRead(): A = this.readI()

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
   *  tries to unlock) it is ignored
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

  public companion object {
    /**
     * Return a new [TVar]
     *
     * More efficient than `atomically { newVar(a) }` because it skips creating a transaction.
     */
    public suspend fun <A> new(a: A): TVar<A> = TVar(a)
  }
}

internal val globalC: AtomicLong = atomic(0L)
