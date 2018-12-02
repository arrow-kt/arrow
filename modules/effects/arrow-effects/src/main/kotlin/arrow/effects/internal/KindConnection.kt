package arrow.effects.internal

import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.Applicative
import java.util.concurrent.atomic.AtomicReference

/**
 * Represents a composite of functions (meant for cancellation) that are stacked. cancel() is idempotent,
 * and all methods are thread-safe & atomic.
 */
sealed class KindConnection<F> {

  /**
   * Cancels the unit of work represented by this reference.
   *
   * Guaranteed idempotency - calling it multiple times should have the same side-effect as calling it only
   * once. Implementations of this method should also be thread-safe.
   */
  abstract fun cancel(): CancelToken<F>

  abstract fun isCanceled(): Boolean

  /**
   * Pushes a function meant to cancel and cleanup resources.
   * These functions are kept inside a stack, and executed in FIFO order on cancellation.
   */
  abstract fun push(token: CancelToken<F>): Unit

  /**
   * Pushes a pair of KindConnection on the stack, which on cancellation will get trampolined. This is useful in
   * race for example, because combining a whole collection of tasks, two by two, can lead to building a
   * cancelable that's stack unsafe.
   */
  abstract fun pushPair(lh: KindConnection<F>, rh: KindConnection<F>): Unit

  /**
   * Pops a cancelable reference from the FIFO stack of references for this connection.
   * A cancelable reference is meant to cancel and cleanup resources.
   *
   * @return the cancelable reference that was removed.
   */
  abstract fun pop(): CancelToken<F>

  /**
   * Tries to reset an IOConnection, from a cancelled state, back to a pristine state, but only if possible.
   *
   * @return true on success, false if there was a race condition (i.e. the connection wasn't cancelled) or if
   * the type of the connection cannot be reactivated.
   */
  abstract fun tryReactivate(): Boolean

  companion object {

    operator fun <F> invoke(MD: MonadDefer<F>): KindConnection<F> = DefaultKindConnection(MD)

    fun <F> uncancelable(FA: Applicative<F>): KindConnection<F> = Uncancelable(FA)
  }

  /**
   * Reusable [IOConnection] reference that cannot be canceled.
   */
  private class Uncancelable<F>(FA: Applicative<F>) : KindConnection<F>(), Applicative<F> by FA {
    override fun cancel(): CancelToken<F> = just(Unit)
    override fun isCanceled(): Boolean = false
    override fun push(token: CancelToken<F>) = Unit
    override fun pop(): CancelToken<F> = just(Unit)
    override fun tryReactivate(): Boolean = true
    override fun pushPair(lh: KindConnection<F>, rh: KindConnection<F>): Unit = Unit
  }

  /**
   * Default [IOConnection] implementation.
   */
  private class DefaultKindConnection<F>(MD: MonadDefer<F>) : KindConnection<F>(), MonadDefer<F> by MD {
    private val state = AtomicReference(emptyList<CancelToken<F>>())

    override fun cancel(): CancelToken<F> = defer {
      state.getAndSet(null).let { list ->
        when {
          list == null || list.isEmpty() -> just(Unit)
          else -> cancelAll(list.iterator())
        }
      }
    }

    override fun isCanceled(): Boolean = state.get() == null

    override tailrec fun push(token: CancelToken<F>): Unit = when (val list = state.get()) {
      null -> if (!state.compareAndSet(list, listOf(token))) push(token) else Unit
      else -> if (!state.compareAndSet(list, listOf(token) + list)) push(token) else Unit
    }

    override fun pushPair(lh: KindConnection<F>, rh: KindConnection<F>): Unit =
      push(cancelAll(listOf(lh.cancel(), rh.cancel()).iterator()))

    override tailrec fun pop(): CancelToken<F> {
      val list = state.get()
      return when {
        list == null || list.isEmpty() -> just(Unit)
        else -> if (!state.compareAndSet(list, list.drop(1))) pop()
        else list.first()
      }
    }

    override fun tryReactivate(): Boolean =
      state.compareAndSet(null, emptyList())

    private fun cancelAll(cursor: Iterator<CancelToken<F>>): CancelToken<F> {
      fun loop(): CancelToken<F> = if (cursor.hasNext()) cursor.next().flatMap { loop() }
      else just(Unit)

      return if (cursor.hasNext()) defer { cursor.next().flatMap { loop() } }
      else just(Unit)
    }

  }

}
