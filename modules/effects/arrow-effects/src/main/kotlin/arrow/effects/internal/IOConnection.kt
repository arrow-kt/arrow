package arrow.effects.internal

import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.typeclasses.Disposable
import java.util.concurrent.atomic.AtomicReference

fun IOConnection.toDisposable(): Disposable = { cancel().fix().unsafeRunSync() }

/**
 * Represents a composite of functions (meant for cancellation) that are stacked. cancel() is idempotent,
 * and all methods are thread-safe & atomic.
 */
sealed class IOConnection {

  /**
   * Cancels the unit of work represented by this reference.
   *
   * Guaranteed idempotency - calling it multiple times should have the same side-effect as calling it only
   * once. Implementations of this method should also be thread-safe.
   */
  abstract fun cancel(): CancelToken<ForIO>

  abstract fun isCanceled(): Boolean

  /**
   * Pushes a cancelable reference on the stack, to be popped or canceled later in FIFO order.
   */
  abstract fun push(token: CancelToken<ForIO>): Unit

  /**
   * Pushes a pair of IOConnection on the stack, which on cancellation will get trampolined. This is useful in
   * IO.race for example, because combining a whole collection of IO tasks, two by two, can lead to building a
   * cancelable that's stack unsafe.
   */
  abstract fun pushPair(lh: IOConnection, rh: IOConnection): Unit

  /**
   * Removes a cancelable reference from the stack in FIFO order.
   *
   * @return the cancelable reference that was removed.
   */
  abstract fun pop(): CancelToken<ForIO>

  /**
   * Tries to reset an IOConnection, from a cancelled state, back to a pristine state, but only if possible.
   *
   * @return true on success, false if there was a race condition (i.e. the connection wasn't cancelled) or if
   * the type of the connection cannot be reactivated.
   */
  abstract fun tryReactivate(): Boolean

  companion object {

    operator fun invoke(): IOConnection = DefaultIOConnection()

    val uncancelable: IOConnection = Uncancelable
  }

  /**
   * Reusable [IOConnection] reference that cannot be canceled.
   */
  private object Uncancelable : IOConnection() {
    override fun cancel(): CancelToken<ForIO> = IO.unit
    override fun isCanceled(): Boolean = false
    override fun push(token: CancelToken<ForIO>) = Unit
    override fun pop(): CancelToken<ForIO> = IO.unit
    override fun tryReactivate(): Boolean = true
    override fun pushPair(lh: IOConnection, rh: IOConnection): Unit = Unit
  }

  /**
   * Default [IOConnection] implementation.
   */
  private class DefaultIOConnection : IOConnection() {
    private val state = AtomicReference(emptyList<CancelToken<ForIO>>())

    override fun cancel(): CancelToken<ForIO> = IO.defer {
      state.getAndSet(null).let { list ->
        when {
          list == null || list.isEmpty() -> IO.unit
          else -> CancelUtils.cancelAll(list.iterator())
        }
      }
    }

    override fun isCanceled(): Boolean = state.get() == null

    override fun push(token: CancelToken<ForIO>): Unit = state.get().let { list ->
      when (list) {
        null -> token.fix().unsafeRunAsync { }
        else -> {
          val update = listOf(token) + list
          if (!state.compareAndSet(list, update)) push(token)
        }
      }
    }

    override fun pushPair(lh: IOConnection, rh: IOConnection): Unit =
      push(CancelUtils.cancelAll(lh.cancel(), rh.cancel()))

    override fun pop(): CancelToken<ForIO> = state.get().let { current ->
      when (current) {
        null, listOf<CancelToken<ForIO>>() -> IO.unit
        else -> // current @ (x::xs)
          if (!state.compareAndSet(current, current.drop(1))) pop()
          else current.first()
      }
    }

    override fun tryReactivate(): Boolean =
      state.compareAndSet(null, emptyList())
  }
}
