package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.typeclasses.MonadDefer

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

/**
 * An asynchronous, concurrent mutable reference.
 *
 * Provides safe concurrent access and modification of its content.
 * [Ref] is a purely functional wrapper over an [AtomicReference] in context [F],
 * that is always initialised to a value [A].
 */
interface Ref<F, A> {

  /**
   * Obtains the current value.
   * Since [Ref] is always guaranteed to have a value, the returned action completes immediately after being bound.
   */
  val get: Kind<F, A>

  /**
   * Sets the current value to [a].
   * The returned action completes after the reference has been successfully set.
   */
  fun set(a: A): Kind<F, Unit>

  /**
   * Replaces the current value with [a], returning the *old* value.
   */
  fun getAndSet(a: A): Kind<F, A>

  /**
   * Replaces the current value with [a], returning the *new* value.
   */
  fun setAndGet(a: A): Kind<F, A>

  /**
   * Updates the current value using the supplied function [f].
   *
   * If another modification occurs between the time the current value is read and subsequently updated,
   * the modification is retried using the new value. Hence, [f] may be invoked multiple times.
   */
  fun update(f: (A) -> A): Kind<F, Unit>

  /**
   * Modifies the current value using the supplied update function and returns the *old* value.
   *
   * @see [update], [f] may be invoked multiple times.
   */
  fun getAndUpdate(f: (A) -> A): Kind<F, A>

  /**
   * Modifies the current value using the supplied update function and returns the *new* value.
   *
   * @see [update], [f] may be invoked multiple times.
   */
  fun updateAndGet(f: (A) -> A): Kind<F, A>

  /**
   * Like [update] but allows the update function to return an output value of type [B].
   */
  fun <B> modify(f: (A) -> Tuple2<A, B>): Kind<F, B>

  /**
   * Attempts to modify the current value once, in contrast to [update] which calls [f] until it succeeds.
   *
   * @returns `false` if concurrent modification completes between the time the variable is read and the time it is set.
   */
  fun tryUpdate(f: (A) -> A): Kind<F, Boolean>

  /**
   * Like [tryUpdate] but allows the update function to return an output value of type [B].
   *
   * @returns [None] if the update fails and `Some(b)` otherwise.
   */
  fun <B> tryModify(f: (A) -> Tuple2<A, B>): Kind<F, Option<B>>

  /**
   * Obtains a snapshot of the current value, and a setter for updating it.
   *
   * The setter will return `false` if another concurrent call invalidated the snapshot (modified the value).
   * It will return `true` if setting the value was successful.
   *
   * Once it has returned false or been used once, a setter never succeeds again.
   */
  fun access(): Kind<F, Tuple2<A, (A) -> Kind<F, Boolean>>>

  companion object {
    /**
     * Builds a [Ref] value for data types given a [MonadDefer] instance
     * without deciding the type of the Ref's value.
     *
     * @see [of]
     */
    operator fun <F> invoke(MD: MonadDefer<F>): PartiallyAppliedRef<F> = object : PartiallyAppliedRef<F> {
      override fun <A> of(a: A): Kind<F, Ref<F, A>> = Ref.of(a, MD)
    }

    /**
     * Creates an asynchronous, concurrent mutable reference initialized to the supplied value.
     */
    fun <F, A> of(a: A, MD: MonadDefer<F>): Kind<F, Ref<F, A>> = MD.delay {
      unsafe(a, MD)
    }

    /**
     * Like [of] but returns the newly allocated ref directly instead of wrapping it in [MonadDefer.invoke].
     * This method is considered unsafe because it is not referentially transparent -- it allocates mutable state.
     *
     * @see [invoke]
     */
    fun <F, A> unsafe(a: A, MD: MonadDefer<F>): Ref<F, A> = MonadDeferRef<F, A>(AtomicReference(a), MD)

    /**
     * Default implementation using based on [MonadDefer] and [AtomicReference]
     */
    private class MonadDeferRef<F, A>(private val ar: AtomicReference<A>, private val MD: MonadDefer<F>) : Ref<F, A> {

      override val get: Kind<F, A>
        get() = MD.delay {
          ar.get()
        }

      override fun set(a: A): Kind<F, Unit> = MD.delay {
        ar.set(a)
      }

      override fun getAndSet(a: A): Kind<F, A> = MD.delay {
        ar.getAndSet(a)
      }

      override fun setAndGet(a: A): Kind<F, A> = MD.run {
        set(a).flatMap { get }
      }

      override fun getAndUpdate(f: (A) -> A): Kind<F, A> = MD.delay {
        ar.getAndUpdate(f)
      }

      override fun updateAndGet(f: (A) -> A): Kind<F, A> = MD.delay {
        ar.updateAndGet(f)
      }

      override fun access(): Kind<F, Tuple2<A, (A) -> Kind<F, Boolean>>> = MD.delay {
        val snapshot = ar.get()
        val hasBeenCalled = AtomicBoolean(false)
        val setter = { a: A ->
          MD.delay { hasBeenCalled.compareAndSet(false, true) && ar.compareAndSet(snapshot, a) }
        }
        Tuple2(snapshot, setter)
      }

      override fun tryUpdate(f: (A) -> A): Kind<F, Boolean> = MD.run {
        tryModify { a -> Tuple2(f(a), Unit) }.map(Option<Unit>::isDefined)
      }

      override fun <B> tryModify(f: (A) -> Tuple2<A, B>): Kind<F, Option<B>> = MD.delay {
        val a = ar.get()
        val (u, b) = f(a)
        if (ar.compareAndSet(a, u)) Some(b)
        else None
      }

      override fun update(f: (A) -> A): Kind<F, Unit> =
        modify { a -> Tuple2(f(a), Unit) }

      override fun <B> modify(f: (A) -> Tuple2<A, B>): Kind<F, B> {
        tailrec fun go(): B {
          val a = ar.get()
          val (u, b) = f(a)
          return if (!ar.compareAndSet(a, u)) go() else b
        }

        return MD.delay(::go)
      }

    }

  }

}

/**
 * Intermediate interface to partially apply [F] to [Ref].
 */
interface PartiallyAppliedRef<F> {
  fun <A> of(a: A): Kind<F, Ref<F, A>>
}