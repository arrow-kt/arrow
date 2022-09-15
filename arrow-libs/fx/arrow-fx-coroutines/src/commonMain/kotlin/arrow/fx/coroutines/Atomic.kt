package arrow.fx.coroutines

import arrow.core.continuations.AtomicRef

/**
 * An [Atomic] with an initial value of [A].
 *
 * [Atomic] wraps `atomic`, so that you can also use it on a top-level function or pass it around.
 * In other languages this data type is also known as `Ref`, `IORef` or Concurrent safe Reference.
 * So in case you don't need to pass around an atomic reference, or use it in top-level functions
 * it's advised to use `atomic` from Atomic Fu directly.
 *
 * ```kotlin
 * import arrow.fx.coroutines.*
 *
 * suspend fun main() {
 *   val count = Atomic(0)
 *
 *   (0 until 20_000).parMap {
 *     count.update(Int::inc)
 *   }
 *   println(count.get())
 * }
 * ```
 * <!--- KNIT example-atomic-01.kt -->
 *
 * [Atomic] also offers some other interesting operators such as [modify], [tryUpdate], [access] & [lens].
 */
public interface Atomic<A> {

  /**
   * Obtains the current value.
   * Since [AtomicRef] is always guaranteed to have a value, the returned action completes immediately after being bound.
   */
  public suspend fun get(): A

  /**
   * Sets the current value to [a].
   * The returned action completes after the reference has been successfully set.
   */
  public suspend fun set(a: A): Unit

  /**
   * Replaces the current value with [a], returning the *old* value.
   */
  public suspend fun getAndSet(a: A): A

  /**
   * Replaces the current value with [a], returning the *new* value.
   */
  public suspend fun setAndGet(a: A): A

  /**
   * Updates the current value using the supplied function [f].
   *
   * If another modification occurs between the time the current value is read and subsequently updated,
   * the modification is retried using the new value. Hence, [f] may be invoked multiple times.
   */
  public suspend fun update(f: (A) -> A): Unit

  /**
   * Modifies the current value using the supplied update function and returns the *old* value.
   *
   * @see [update], [f] may be invoked multiple times.
   */
  public suspend fun getAndUpdate(f: (A) -> A): A

  /**
   * Modifies the current value using the supplied update function and returns the *new* value.
   *
   * @see [update], [f] may be invoked multiple times.
   */
  public suspend fun updateAndGet(f: (A) -> A): A

  /**
   * Modify allows to inspect the state [A] of the [AtomicRef], update it and extract a different state [B].
   *
   * ```kotlin
   * import arrow.fx.coroutines.*
   *
   * typealias Id = Int
   * data class Job(val description: String)
   *
   * val initialState = (0 until 10).map { i -> Pair(i, Job("Task #$i")) }
   *
   * suspend fun main(): Unit {
   *   val jobs = Atomic(initialState)
   *
   *   val batch = jobs.modify { j ->
   *     val batch = j.take(5)
   *     Pair(j.drop(5), batch)
   *   }
   *
   *   batch.forEach { (id, job) ->
   *     println("Going to work on $job with id $id\n")
   *   }
   *
   *   println("Remaining: ${jobs.get()}")
   * }
   * ```
 * <!--- KNIT example-atomic-02.kt -->
   */
  public suspend fun <B> modify(f: (A) -> Pair<A, B>): B

  /**
   * ModifyGet allows to inspect state [A], update it and extract a different state [B].
   * In contrast to [modify], it returns a [Pair] of the updated state [A] and the extracted state [B].
   *
   * @see [modify] for an example
   */
  public suspend fun <B> modifyGet(f: (A) -> Pair<A, B>): Pair<A, B>

  /**
   * Attempts to modify the current value once, in contrast to [update] which calls [f] until it succeeds.
   *
   * @returns `false` if concurrent modification completes between the time the variable is read and the time it is set.
   */
  public suspend fun tryUpdate(f: (A) -> A): Boolean

  /**
   * Attempts to inspect the state, uptade it, and extract a different state.
   *
   * [tryModify] behaves as [tryUpdate] but allows the update function to return an output value of type [B].
   *
   * @returns `null` if the update fails and [B] otherwise.
   */
  public suspend fun <B> tryModify(f: (A) -> Pair<A, B>): B?

  /**
   * Obtains a snapshot of the current value, and a setter for updating it.
   *
   * This is useful when you need to execute effects with the original result while still ensuring an atomic update.
   *
   * The setter will return `false` if another concurrent call invalidated the snapshot (modified the value).
   * It will return `true` if setting the value was successful.
   *
   * Once it has returned `false` or been used once, a setter never succeeds again.
   */
  public suspend fun access(): Pair<A, suspend (A) -> Boolean>

  /**
   * Creates an [AtomicRef] for [B] based on provided a [get] and [set] operation.
   *
   * This is useful when you have an [AtomicRef] of a `data class`
   * and need to work with with certain properties individually,
   * or want to hide parts of your domain from a dependency.
   *
   * ```kotlin
   * import arrow.fx.coroutines.*
   *
   * data class Preference(val isEnabled: Boolean)
   * data class User(val name: String, val age: Int, val preference: Preference)
   * data class ViewState(val user: User)
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val state: Atomic<ViewState> = Atomic(ViewState(User("Simon", 27, Preference(false))))
   *   val isEnabled: Atomic<Boolean> =
   *     state.lens(
   *       { it.user.preference.isEnabled },
   *       { state, isEnabled ->
   *         state.copy(
   *           user =
   *           state.user.copy(
   *             preference =
   *             state.user.preference.copy(isEnabled = isEnabled)
   *           )
   *         )
   *       }
   *     )
   *   isEnabled.set(true)
   *   println(state.get())
   * }
   * ```
 * <!--- KNIT example-atomic-03.kt -->
   */
  public fun <B> lens(get: (A) -> B, set: (A, B) -> A): arrow.fx.coroutines.Atomic<B> =
    LensAtomic(this, get, set)

  public companion object {

    /**
     * Creates an [AtomicRef] with an initial value of [A].
     *
     * Data type on top of [atomic] to use in parallel functions.
     *
     * ```kotlin
     * import arrow.fx.coroutines.*
     *
     * suspend fun main() {
     *   val count = Atomic(0)
     *   (0 until 20_000).parMap {
     *     count.update(Int::inc)
     *   }
     *   println(count.get())
     * }
     * ```
 * <!--- KNIT example-atomic-04.kt -->
     */
    public suspend operator fun <A> invoke(a: A): arrow.fx.coroutines.Atomic<A> = unsafe(a)
    public fun <A> unsafe(a: A): arrow.fx.coroutines.Atomic<A> = DefaultAtomic(a)
  }
}

private class DefaultAtomic<A>(a: A) : arrow.fx.coroutines.Atomic<A> {

  private val ar = AtomicRef(a)

  public override suspend fun get(): A =
    ar.get()

  public override suspend fun set(a: A): Unit {
    ar.set(a)
  }

  public override suspend fun getAndSet(a: A): A =
    ar.getAndSet(a)

  public override suspend fun setAndGet(a: A): A {
    ar.set(a)
    return a
  }

  public override suspend fun getAndUpdate(f: (A) -> A): A {
    while (true) {
      val cur = get()
      val upd = f(cur)
      if (ar.compareAndSet(cur, upd)) return cur
    }
  }

  public override suspend fun updateAndGet(f: (A) -> A): A {
    while (true) {
      val cur = ar.get()
      val upd = f(cur)
      if (ar.compareAndSet(cur, upd)) return upd
    }
  }

  public override suspend fun access(): Pair<A, suspend (A) -> Boolean> {
    val snapshot = ar.get()
    val hasBeenCalled = AtomicRef(false)
    val setter: suspend (A) -> Boolean = { a: A ->
      hasBeenCalled.compareAndSet(false, true) && ar.compareAndSet(snapshot, a)
    }

    return Pair(snapshot, setter)
  }

  public override suspend fun tryUpdate(f: (A) -> A): Boolean =
    tryModify { a -> Pair(f(a), Unit) } != null

  public override suspend fun <B> tryModify(f: (A) -> Pair<A, B>): B? {
    val a = ar.get()
    val (u, b) = f(a)
    return if (ar.compareAndSet(a, u)) b
    else null
  }

  public override suspend fun update(f: (A) -> A): Unit =
    modify { a -> Pair(f(a), Unit) }

  public override suspend fun <B> modify(f: (A) -> Pair<A, B>): B {
    tailrec fun go(): B {
      val a = ar.get()
      val (u, b) = f(a)
      return if (!ar.compareAndSet(a, u)) go() else b
    }

    return go()
  }

  public override suspend fun <B> modifyGet(f: (A) -> Pair<A, B>): Pair<A, B> {
    tailrec fun go(): Pair<A, B> {
      val a = ar.get()
      val res = f(a)
      return if (!ar.compareAndSet(a, res.first)) go() else res
    }

    return go()
  }
}

private class LensAtomic<A, B>(
  private val underlying: arrow.fx.coroutines.Atomic<A>,
  private val lensGet: (A) -> B,
  private val lensSet: (A, B) -> A
) : arrow.fx.coroutines.Atomic<B> {

  public override suspend fun setAndGet(a: B): B =
    underlying.modify { old ->
      Pair(lensModify(old) { a }, a)
    }

  public override suspend fun getAndUpdate(f: (B) -> B): B =
    underlying.modify { old ->
      Pair(lensModify(old, f), lensGet(old))
    }

  public override suspend fun updateAndGet(f: (B) -> B): B =
    underlying.modify { old ->
      val new = lensModify(old, f)
      Pair(new, lensGet(new))
    }

  public override suspend fun get(): B =
    lensGet(underlying.get())

  public override suspend fun set(a: B) {
    underlying.update { old -> lensModify(old) { a } }
  }

  public override suspend fun getAndSet(a: B): B =
    underlying.modify { old ->
      Pair(lensModify(old) { a }, lensGet(old))
    }

  public override suspend fun update(f: (B) -> B) =
    underlying.update { old -> lensModify(old, f) }

  public override suspend fun <C> modify(f: (B) -> Pair<B, C>): C =
    underlying.modify { old ->
      val oldB = lensGet(old)
      val (b, c) = f(oldB)
      Pair(lensSet(old, b), c)
    }

  public override suspend fun <C> modifyGet(f: (B) -> Pair<B, C>): Pair<B, C> =
    underlying.modifyGet { old ->
      val oldB = lensGet(old)
      val (b, c) = f(oldB)
      Pair(lensSet(old, b), c)
    }.let { (a, c) -> Pair(lensGet(a), c) }

  public override suspend fun tryUpdate(f: (B) -> B): Boolean =
    tryModify { b -> Pair(f(b), Unit) } != null

  public override suspend fun <C> tryModify(f: (B) -> Pair<B, C>): C? =
    underlying.tryModify { a ->
      val oldB = lensGet(a)
      val (b, result) = f(oldB)
      Pair(lensSet(a, b), result)
    }

  public override suspend fun access(): Pair<B, suspend (B) -> Boolean> {
    val snapshotA = underlying.get()
    val snapshotB = lensGet(snapshotA)

    val setter: suspend (B) -> Boolean = { b: B ->
      val hasBeenCalled = AtomicRef(false)

      suspend {
        val called = hasBeenCalled.compareAndSet(false, true)

        underlying.tryModify { a ->
          if (called && lensGet(a) == snapshotA) Pair(lensSet(a, b), true)
          else Pair(a, false)
        } ?: false
      }.invoke()
    }

    return Pair(snapshotB, setter)
  }

  private fun lensModify(s: A, f: (B) -> B): A =
    lensSet(s, f(lensGet(s)))
}
