package arrow.effects.internal

import arrow.effects.ForIO
import arrow.effects.typeclasses.Async
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
   * Pushes a cancelable reference on the stack, to be popped or canceled later in FIFO order.
   */
  abstract fun push(token: CancelToken<F>): Unit

  /**
   * Pushes a pair of IOConnection on the stack, which on cancellation will get trampolined. This is useful in
   * IO.race for example, because combining a whole collection of IO tasks, two by two, can lead to building a
   * cancelable that's stack unsafe.
   */
  abstract fun pushPair(lh: KindConnection<F>, rh: KindConnection<F>): Unit

  /**
   * Removes a cancelable reference from the stack in FIFO order.
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

    operator fun <F> invoke(AS: Async<F>): KindConnection<F> = DefaultKindConnection(AS)

    fun <F> uncancelable(FA: Applicative<F>): KindConnection<F> = Uncancelable(FA)
  }

  /**
   * Reusable [IOConnection] reference that cannot be canceled.
   */
  private class Uncancelable<F>(val FA: Applicative<F>) : KindConnection<F>() {
    override fun cancel(): CancelToken<F> = FA.just(Unit)
    override fun isCanceled(): Boolean = false
    override fun push(token: CancelToken<F>) = Unit
    override fun pop(): CancelToken<F> = FA.just(Unit)
    override fun tryReactivate(): Boolean = true
    override fun pushPair(lh: KindConnection<F>, rh: KindConnection<F>): Unit = Unit
  }

  /**
   * Default [IOConnection] implementation.
   */
  private class DefaultKindConnection<F>(val AS: Async<F>) : KindConnection<F>() {
    private val state = AtomicReference(emptyList<CancelToken<F>>())

    override fun cancel(): CancelToken<F> = AS.defer {
      state.getAndSet(null).let { list ->
        when {
          list == null || list.isEmpty() -> AS.just(Unit)
          else -> CancelUtils2.cancelAll(list.iterator(), AS)
        }
      }
    }

    override fun isCanceled(): Boolean = state.get() == null

    override fun push(token: CancelToken<F>): Unit = state.get().let { list ->
      when (list) {
//        null -> token.fix().unsafeRunAsync { } TODO("Can this ever occur??? & why do we need to run on push???")
        else -> {
          val update = listOf(token) + list
          if (!state.compareAndSet(list, update)) push(token)
        }
      }
    }

    override fun pushPair(lh: KindConnection<F>, rh: KindConnection<F>): Unit =
      push(CancelUtils2.cancelAll(listOf(lh.cancel(), rh.cancel()).iterator(), AS))

    override fun pop(): CancelToken<F> = state.get().let { current ->
      when (current) {
        null, listOf<CancelToken<ForIO>>() -> AS.just(Unit)
        else -> // current @ (x::xs)
          if (!state.compareAndSet(current, current.drop(1))) pop()
          else current.first()
      }
    }

    override fun tryReactivate(): Boolean =
      state.compareAndSet(null, emptyList())
  }
}

internal object CancelUtils2 {

  fun <F> cancelAll(cursor: Iterator<CancelToken<F>>, AS: Async<F>): CancelToken<F> =
    if (!cursor.hasNext()) AS.just(Unit)
    else AS.defer {
      val frame = CancelAllFrame2(cursor, AS)
      frame.loop()
    }

  /**
   * Optimization for cancelAll
   */
  private class CancelAllFrame2<F>(val cursor: Iterator<CancelToken<F>>, AS: Async<F>) : Async<F> by AS {

    private var errors = listOf<Throwable>()

    fun loop(): CancelToken<F> =
      if (cursor.hasNext()) cursor.next().flatMap { loop() }
      else when (errors) {
        emptyList<Throwable>() -> just(Unit)
        else -> raiseError(ErrorUtils.composeErrors(errors.first(), errors.drop(1)))
      }

  }


}