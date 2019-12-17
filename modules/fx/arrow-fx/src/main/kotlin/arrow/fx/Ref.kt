package arrow.fx

import arrow.Kind
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.internal.AtomicBooleanW
import arrow.core.invoke
import arrow.fx.typeclasses.MonadDefer
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.getAndUpdate
import kotlinx.atomicfu.updateAndGet

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
  fun get(): Kind<F, A>

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
     * Creates an asynchronous, concurrent mutable reference initialized using the supplied function.
     */
    operator fun <F, A> invoke(MD: MonadDefer<F>, a: A): Kind<F, Ref<F, A>> = MD.later {
      unsafe(a, MD)
    }

    /**
     * Like [invoke] but returns the newly allocated ref directly instead of wrapping it in [MonadDefer.invoke].
     * This method is considered unsafe because it is not referentially transparent -- it allocates mutable state.
     *
     * @see [invoke]
     */
    fun <F, A> unsafe(a: A, MD: MonadDefer<F>): Ref<F, A> = MonadDeferRef(a, MD)

    /**
     * Build a [RefFactory] value for creating Ref types [F] without deciding the type of the Ref's value.
     *
     * @see RefFactory
     */
    fun <F> factory(MD: MonadDefer<F>) = object : RefFactory<F> {
      override fun <A> just(a: A): Kind<F, Ref<F, A>> = Ref(MD, a)
    }

    /**
     * Default implementation using based on [MonadDefer] and [AtomicReference]
     */
    private class MonadDeferRef<F, A>(a: A, private val MD: MonadDefer<F>) : Ref<F, A> {

      private val ar: AtomicRef<A> = atomic(a)

      override fun get(): Kind<F, A> = MD.later {
        ar.value
      }

      override fun set(a: A): Kind<F, Unit> = MD.later {
        ar.value = a
      }

      override fun getAndSet(a: A): Kind<F, A> = MD.later {
        ar.getAndSet(a)
      }

      override fun setAndGet(a: A): Kind<F, A> = MD.run {
        set(a).flatMap { get() }
      }

      override fun getAndUpdate(f: (A) -> A): Kind<F, A> = MD.later {
        ar.getAndUpdate(f)
      }

      override fun updateAndGet(f: (A) -> A): Kind<F, A> = MD.later {
        ar.updateAndGet(f)
      }

      override fun access(): Kind<F, Tuple2<A, (A) -> Kind<F, Boolean>>> = MD.later {
        val snapshot = ar.value
        val hasBeenCalled = AtomicBooleanW(false)
        val setter = { a: A ->
          MD.later { hasBeenCalled.compareAndSet(false, true) && ar.compareAndSet(snapshot, a) }
        }
        Tuple2(snapshot, setter)
      }

      override fun tryUpdate(f: (A) -> A): Kind<F, Boolean> = MD.run {
        tryModify { a -> Tuple2(f(a), Unit) }
          .map(Option<Unit>::isDefined)
      }

      override fun <B> tryModify(f: (A) -> Tuple2<A, B>): Kind<F, Option<B>> = MD.later {
        val a = ar.value
        val (u, b) = f(a)
        if (ar.compareAndSet(a, u)) Some(b)
        else None
      }

      override fun update(f: (A) -> A): Kind<F, Unit> =
        modify { a -> Tuple2(f(a), Unit) }

      override fun <B> modify(f: (A) -> Tuple2<A, B>): Kind<F, B> {
        tailrec fun go(): B {
          val a = ar.value
          val (u, b) = f(a)
          return if (!ar.compareAndSet(a, u)) go() else b
        }

        return MD.later(::go)
      }
    }
  }
}

/**
 * Builds a [Ref] value for data types [F]
 * without deciding the type of the Ref's value.
 *
 * ```kotlin:ank:playground
 * import arrow.fx.*
 * import arrow.fx.extensions.io.monadDefer.monadDefer
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val refFactory: RefFactory<ForIO> = Ref.factory(IO.monadDefer())
 *   val intVar: IOOf<Ref<ForIO, Int>> = refFactory.just(5)
 *   val stringVar: IOOf<Ref<ForIO, String>> = refFactory.just("Hello")
 *   //sampleEnd
 * }
 * ```
 */
interface RefFactory<F> {

  /**
   * Builds a [MVar] with a value of type [A].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.*
   * import arrow.fx.extensions.io.async.async
   *
   * fun main(args: Array<String>) {
   *   //sampleStart
   *   val refFactory: RefFactory<ForIO> = Ref.factory(IO.async())
   *   val intVar: IOOf<Ref<ForIO, Int>> = refFactory.just(5)
   *   //sampleEnd
   * }
   * ```
   */
  fun <A> just(a: A): Kind<F, Ref<F, A>>
}
