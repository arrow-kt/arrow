@file:OptIn(ExperimentalContracts::class, ExperimentalAtomicApi::class)

package arrow.fx.stm.internal

import arrow.fx.stm.STM
import arrow.fx.stm.TVar
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.Continuation

/**
 * A STMFrame keeps the reads and writes performed by a transaction.
 * It may have a parent which is only used for read lookups.
 */
internal class STMFrame(private val parent: STMFrame? = null) : STM {

  class Entry(var initialVal: Any?, var newVal: Any?) {
    object NoChange
    object NotPresent

    fun isWrite(): Boolean =
      newVal !== NoChange

    fun update(v: Any?) {
      newVal = if (initialVal === v) NoChange else v
    }

    fun getValue(): Any? = if (isWrite()) newVal else initialVal
  }

  internal val accessMap = mutableMapOf<TVar<Any?>, Entry>()

  /**
   * Helper to search the entire hierarchy for stored previous reads
   */
  private fun readVar(v: TVar<Any?>): Any =
    accessMap[v]?.getValue() ?: parent?.readVar(v) ?: Entry.NotPresent

  override fun retry(): Nothing = throw RetryException

  override fun <A> (STM.() -> A).orElse(other: STM.() -> A): A =
    runLocal(this@orElse, { this@STMFrame.other() }) { throw it }

  override fun <A> catch(f: STM.() -> A, onError: STM.(Throwable) -> A): A =
    runLocal(f, { this@STMFrame.retry() }) { this@STMFrame.onError(it) }

  private inline fun <A> runLocal(
    f: STM.() -> A,
    onRetry: () -> A,
    onError: (Throwable) -> A
  ): A {
    while (true) {
      val frame = STMFrame(this@STMFrame)
      try {
        val res = frame.f()

        // Validate the inner frame right now to check for a quick early abort and a cheaper retry
        //  If we are already invalid here there is no point in continuing.
        if (frame.validate()) {
          this@STMFrame.merge(frame)
          return res
        }
      } catch (ignored: RetryException) {
        if (frame.validate()) {
          this@STMFrame.mergeReads(frame)
          return onRetry()
        }
      } catch (e: Throwable) {
        // An invalid frame retries even if it throws, so our sub-frame also needs to handle this correctly
        if (frame.validate()) {
          this@STMFrame.mergeReads(frame)
          return onError(e)
        }
      }
    }
  }

  /**
   * First checks if we have already read this variable, if not it reads it and stores the result
   */
  @Suppress("UNCHECKED_CAST")
  override fun <A> TVar<A>.read(): A =
    when (val r = readVar(this as TVar<Any?>)) {
      Entry.NotPresent -> readI().also { accessMap[this] = Entry(it, Entry.NoChange) }
      else -> r as A
    }

  /**
   * Add a write to the write set.
   *
   * If we have not seen this variable before we add a read which stores it in the read set as well.
   */
  @Suppress("UNCHECKED_CAST")
  override fun <A> TVar<A>.write(a: A) {
    this as TVar<Any?>
    accessMap[this]?.update(a) ?: readI().let { accessMap[this] = Entry(it, a) }
  }

  internal fun validate(): Boolean =
    accessMap.all { (tv, entry) -> tv.value === entry.initialVal }

  internal fun validateAndCommit(): Boolean {
    if (accessMap.isEmpty()) return true

    val locked = mutableListOf<Map.Entry<TVar<Any?>, Entry>>()
    val reads = mutableListOf<Map.Entry<TVar<Any?>, Entry>>()

    /**
     * Why do we not lock reads?
     * To answer this question we need to ask under what conditions a transaction may commit:
     * - A transaction can commit if all values read contain the same value when committing
     *
     * This means that when we hold all write locks we just need to verify that all our reads are consistent, any change after
     *  that has no effect on this transaction because our write will 100% persist consistently (we hold all locks) and
     *  any other transaction depending on a variable we are about to write to has to wait for us and then verify again
     */
    accessMap.forEach { tvToEntry ->
      val (tv, entry) = tvToEntry
      if (entry.isWrite()) {
        if (tv.lock_cond(this, entry.initialVal)) {
          locked.add(tvToEntry)
        } else {
          locked.forEach { it.key.release(this, it.value.initialVal) }
          return@validateAndCommit false
        }
      } else {
        if (tv.value !== entry.initialVal) {
          locked.forEach { it.key.release(this, it.value.initialVal) }
          return@validateAndCommit false
        } else {
          reads.add(tvToEntry)
        }
      }
    }

    if (reads.any { (tv, entry) -> tv.value !== entry.initialVal }) {
      locked.forEach { it.key.release(this, it.value.initialVal) }
      return false
    }

    locked.forEach { it.key.release(this, it.value.newVal) }
    // TODO Evaluate if this needs to be separate or if it is cheap enough to do above.
    //  Basically any work done before all locks are released needs to be cheap and this avoids a bit of work.
    locked.forEach { it.key.notify() }
    return true
  }

  private fun mergeReads(other: STMFrame) {
    accessMap.putAll(other.accessMap.filter { (_, e) -> e.isWrite().not() })
  }

  private fun merge(other: STMFrame) {
    accessMap.putAll(other.accessMap)
  }
}

/**
 * In some special cases it is possible to detect if a STM transaction blocks indefinitely so we can
 *  abort here.
 */
public class BlockedIndefinitely : Throwable("Transaction blocked indefinitely")

public expect object RetryException : Throwable

// --------
/**
 * Wrapper for a running transaction.
 *
 * Keeps the continuation that [TVar]'s use to resume this transaction.
 */
internal class STMTransaction {
  private val cont = AtomicReference<Continuation<Unit>?>(null)

  /**
   * Any one resumptions is enough, because we enqueue on all read variables this might be called multiple times.
   */
  fun getCont(): Continuation<Unit>? = cont.exchange(null)

  // TODO should we abort after retrying x times to help a user notice "live-locked" transactions?
  //  This could be implemented by checking two values when retrying:
  //  - the number of prior retries
  //  - the time since we started trying to commit this transaction
  //  If they both pass a threshold we should probably kill the transaction and throw
  //  "live-locked" transactions are those that are continuously retry due to accessing variables with high contention and
  //   taking longer than the transactions updating those variables.
  suspend fun <A> commit(f: STM.() -> A): A {
    contract {
      callsInPlace(f, InvocationKind.AT_LEAST_ONCE)
    }
    loop@ while (true) {
      val frame = STMFrame()
      try {
        val res = frame.f()

        if (frame.validateAndCommit()) return res
      } catch (ignored: RetryException) {
        if (frame.accessMap.isEmpty()) throw BlockedIndefinitely()

        val registered = mutableListOf<TVar<Any?>>()
        suspendCancellableCoroutine susp@{ k ->
          cont.store(k)

          frame.accessMap
            .forEach { (tv, entry) ->
              if (tv.registerWaiting(this, entry.initialVal)) registered.add(tv)
              else return@susp
            }
        }
        registered.forEach { it.removeWaiting(this) }
      } catch (e: Throwable) {
        if (frame.validate()) throw e
      }
    }
  }
}
