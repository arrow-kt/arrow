package arrow.fx.coroutines

import arrow.core.Tuple2
import arrow.core.toT
import arrow.fx.coroutines.DefaultConcurrentVar.Companion.State.WaitForPut
import arrow.fx.coroutines.DefaultConcurrentVar.Companion.State.WaitForTake
import kotlinx.atomicfu.atomic

/**
 * [ConcurrentVar] is a mutable concurrent safe variable which is either `empty` or contains a `single value` of type [A].
 * It behaves as a single element [arrow.fx.coroutines.stream.concurrent.Queue].
 * When trying to [put] or [take], it will suspend when it is respectively [isEmpty] or [isNotEmpty].
 *
 * There are also operators that return immediately, [tryTake] & [tryPut],
 * since checking [isEmpty] could be outdated immediately.
 *
 * [ConcurrentVar] is appropriate for building synchronization primitives and performing simple inter-thread communications.
 * i.e. in situations where you want to `suspend` until the [ConcurrentVar] is initialised with a value [A].
 *
 * ```kotlin:ank:playground
 * import arrow.fx.coroutines.*
 *
 * suspend fun main(): Unit {
 *   val mvar = ConcurrentVar.empty<Int>()
 *
 *   ForkConnected {
 *     sleep(3.seconds)
 *     mvar.put(5)
 *   }
 *
 *  val r = mvar.take() // suspend until Fork puts result in ConcurrentVar
 *  println(r)
 * }
 * ```
 *
 * ## Using [ConcurrentVar] as a lock safely
 *
 * [ConcurrentVar] can also be used as a lock if every operation calls [take], does work and then [put]'s the value back.
 * However this is quite unsafe if operations can be cancelled or can throw exception while they hold a lock.
 * The best approach to overcome this is to use [bracketCase] however since this is a rather common pattern, it is made available with [withConcurrentVar], [modify] and [modify_].
 *
 * > Note that this only works if all operations over the [ConcurrentVar] follow the pattern of first taking and then putting back both exactly once and in order.
 *  Or use the helpers to also be safe in case of exceptions and cancellation.
 */
interface ConcurrentVar<A> {

  /**
   * Returns true if there are no elements. Otherwise false.
   * This may be outdated immediately; use [tryPut] or [tryTake] to [put] & [take] without suspending.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val empty = ConcurrentVar.empty<Int>().isEmpty()
   *   val full = ConcurrentVar(10).isEmpty()
   *   //sampleEnd
   *   println("empty: $empty, full: $full")
   * }
   *```
   *
   */
  suspend fun isEmpty(): Boolean

  /**
   * Returns true if there no elements. Otherwise false.
   * This may be outdated immediately; use [tryPut] or [tryTake] to [put] & [take] without suspending.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val empty = ConcurrentVar.empty<Int>().isNotEmpty()
   *   val full = ConcurrentVar(10).isNotEmpty()
   *   //sampleEnd
   *   println("empty: $empty, full: $full")
   * }
   *```
   */
  suspend fun isNotEmpty(): Boolean

  /**
   * Puts [A] in the [ConcurrentVar] if it is empty, or suspends if full until the given value is next in line
   * to be consumed by [take].
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val mvar = ConcurrentVar.empty<Int>()
   *   mvar.put(5)
   *   val none = timeOutOrNull(1.seconds) {
   *     mvar.put(10)
   *   }
   *   val res = mvar.take()
   *   //sampleEnd
   *   println("none: $none, res: $res")
   * }
   *```
   *
   * @see [tryPut] for an operator that doesn't suspend if the [ConcurrentVar] is filled.
   */
  suspend fun put(a: A): Unit

  /**
   * Tries to put [A] in the [ConcurrentVar] if it is empty,
   * returns immediately with true if successfully put the value in the [ConcurrentVar] or false otherwise.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val mvar = ConcurrentVar.empty<Int>()
   *   val succeed = mvar.tryPut(5)
   *   val failed = mvar.tryPut(10)
   *   val res = mvar.take()
   *   //sampleEnd
   *   println("succeed: $succeed, failed: $failed, res: $res")
   * }
   *```
   *
   * @see [tryPut] for an operator that doesn't suspend if the [ConcurrentVar] is filled.
   */
  suspend fun tryPut(a: A): Boolean

  /**
   * Empties the [ConcurrentVar] if full, returning the value,
   * or suspend until a value is available.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   * //sampleStart
   * val mvar = ConcurrentVar(5)
   * val five = mvar.take()
   * val none = timeOutOrNull(1.seconds) {
   *   mvar.take()
   * }
   * //sampleEnd
   * println("five: $five, none: $none")
   * }
   *```
   */
  suspend fun take(): A

  /**
   * Tries to take the value of [ConcurrentVar], returns a value immediately if the [ConcurrentVar] is not
   * empty, or null otherwise.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val mvar = ConcurrentVar(5)
   *   val value = mvar.tryTake()
   *   val empty = mvar.tryTake()
   *   //sampleEnd
   *   println("value: $value, empty: $empty")
   * }
   *```
   */
  suspend fun tryTake(): A?

  /**
   * Reads the current value without emptying the [ConcurrentVar], assuming there is one, or otherwise
   * it suspends until there is a value available.
   *
   * ```kotlin:ank:playground
   * import arrow.fx.coroutines.*
   *
   * suspend fun main(): Unit {
   *   //sampleStart
   *   val mvar = ConcurrentVar.empty<Int>()
   *
   *   val none = timeOutOrNull(1.seconds) {
   *     mvar.read()
   *   }
   *   mvar.put(10)
   *   val read1 = mvar.read()
   *   val read2 =mvar.read()
   *   //sampleEnd
   *   println("none: $none, read1: $read1, read2: $read2")
   * }
   *```
   */
  suspend fun read(): A

  /**
   * Exception- and Cancellation-safe wrapper for operating on the contents of a [ConcurrentVar].
   *
   * Should an exception occur during [f]'s execution, or if it is cancelled, the value will always be put back.
   *
   * This operation is only atomic if there are no other producers for this [ConcurrentVar].
   */
  suspend fun <B> withConcurrentVar(f: suspend (A) -> B): B

  /**
   * Exception- and Cancellation-safe wrapper for modifying the contents of a [ConcurrentVar].
   *
   * Should an exception occur during [f]'s execution, or if it is cancelled, the initial value will be put back.
   *
   * This operation is only atomic if there are no other producers for this [ConcurrentVar].
   *
   * @see [modify_] A version that returns unit and does not expect a Tuple
   */
  suspend fun <B> modify(f: suspend (A) -> Tuple2<A, B>): B

  /**
   * Exception- and Cancellation-safe wrapper for modifying the contents of a [ConcurrentVar].
   *
   * Should an exception occur during [f]'s execution, or if it is cancelled, the initial value will be put back.
   *
   * This operation is only atomic if there are no other producers for this [ConcurrentVar].
   *
   * @see [modify] A version that allows a custom return value instead of unit.
   */
  suspend fun modify_(f: suspend (A) -> A): Unit = modify { f(it) toT Unit }

  companion object {
    /** Builds a [ConcurrentVar] instance with an [initial] value. */
    suspend operator fun <A> invoke(initial: A): ConcurrentVar<A> =
      DefaultConcurrentVar(DefaultConcurrentVar.Companion.State(initial))

    /** Returns an empty [ConcurrentVar] instance. */
    suspend fun <A> empty(): ConcurrentVar<A> =
      DefaultConcurrentVar(DefaultConcurrentVar.Companion.State.empty())

    fun <A> unsafe(initial: A): ConcurrentVar<A> =
      DefaultConcurrentVar(DefaultConcurrentVar.Companion.State(initial))

    fun <A> unsafeEmpty(): ConcurrentVar<A> =
      DefaultConcurrentVar(DefaultConcurrentVar.Companion.State.empty())
  }
}

internal sealed class Maybe<out A> {
  object None : Maybe<Nothing>()
  data class Just<A>(val value: A) : Maybe<A>()
}

internal fun <A> Maybe<A>.orElse(f: () -> Maybe<A>): Maybe<A> =
  when (this) {
    Maybe.None -> f()
    is Maybe.Just -> this
  }

internal fun <A> Maybe<A>.getOrElse(f: () -> A): A =
  when (this) {
    Maybe.None -> f()
    is Maybe.Just -> this.value
  }

internal fun <A> A?.toMaybe(): Maybe<A> =
  if (this == null) Maybe.None else Maybe.Just(this) as Maybe<A>

private class DefaultConcurrentVar<A> constructor(initial: State<A>) : ConcurrentVar<A> {

  private val state = atomic(initial)

  override suspend fun isEmpty(): Boolean =
    when (state.value) {
      is WaitForPut -> true
      is WaitForTake -> false
    }

  override suspend fun isNotEmpty(): Boolean =
    when (state.value) {
      is WaitForPut -> false
      is WaitForTake -> true
    }

  override suspend fun put(a: A): Unit =
    if (tryPut(a)) Unit
    else {
      cancelBoundary()
      cancellableF { cb -> unsafePut(a, cb) }
    }

  override suspend fun tryPut(a: A): Boolean =
    unsafeTryPut(a)

  override suspend fun take(): A =
    when (val m = unsafeTryTake()) {
      Maybe.None -> cancellableF { unsafeTake(it) }
      is Maybe.Just -> m.value
    }

  override suspend fun tryTake(): A? =
    when (val m = unsafeTryTake()) {
      Maybe.None -> null
      is Maybe.Just -> m.value
    }

  override suspend fun read(): A =
    cancellable(::unsafeRead)

  override suspend fun <B> withConcurrentVar(f: suspend (A) -> B): B =
    bracketCase(
      acquire = ::take,
      use = f,
      release = { a, _ -> put(a) }
    )

  override suspend fun <B> modify(f: suspend (A) -> Tuple2<A, B>): B {
    // ugly. Is there a better way?
    var res: A? = null
    return bracketCase(
      acquire = ::take,
      use = {
        val (a, b) = f(it)
        res = a
        b
      },
      release = { a, exit ->
        when (exit) {
          is ExitCase.Failure -> put(a)
          is ExitCase.Cancelled -> put(a)
          is ExitCase.Completed -> put(res!!)
        }
      }
    )
  }

  private tailrec suspend fun unsafeTryPut(a: A): Boolean =
    when (val current = state.value) {
      is WaitForTake -> false
      is WaitForPut -> {
        var first: Listener<A>? = null
        val update: State<A> = if (current.takes.isEmpty()) {
          State(a)
        } else {
          first = current.takes.values.first()
          val rest = current.takes.entries.drop(1)
          if (rest.isEmpty()) State.empty()
          else WaitForPut(emptyMap(), rest.toMap())
        }

        if (!state.compareAndSet(current, update)) {
          unsafeTryPut(a)
        } else if (first != null || current.reads.isNotEmpty()) {
          callPutAndAllReaders(a, first, current.reads)
        } else true
      }
    }

  private tailrec suspend fun unsafePut(a: A, onPut: Listener<Unit>): CancelToken =
    when (val current = state.value) {
      is WaitForTake -> {
        val id = Token()
        val newMap = current.listeners + Pair(id, Pair(a, onPut))
        if (state.compareAndSet(current, WaitForTake(current.value, newMap))) CancelToken { unsafeCancelPut(id) }
        else unsafePut(a, onPut)
      }
      is WaitForPut -> {
        var first: Listener<A>? = null
        val update = if (current.takes.isEmpty()) {
          State(a)
        } else {
          first = current.takes.values.first()
          val rest = current.takes.entries.drop(1)
          if (rest.isEmpty()) State.empty()
          else WaitForPut(emptyMap(), rest.toMap())
        }

        if (state.compareAndSet(current, update)) {
          if (first != null || current.reads.isNotEmpty()) {
            callPutAndAllReaders(a, first, current.reads)
            onPut(Result.success(Unit))
            CancelToken.unit
          } else {
            onPut(Result.success(Unit))
            CancelToken.unit
          }
        } else unsafePut(a, onPut)
      }
    }

  private tailrec fun unsafeCancelPut(id: Token): Unit =
    when (val current = state.value) {
      is WaitForTake -> {
        val update = current.copy(listeners = current.listeners - id)
        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelPut(id)
      }
      is WaitForPut -> Unit
    }

  private tailrec suspend fun unsafeTryTake(): Maybe<A> =
    when (val current = state.value) {
      is WaitForTake -> {
        if (current.listeners.isEmpty()) {
          if (state.compareAndSet(current, State.empty())) Maybe.Just(current.value)
          else unsafeTryTake()
        } else {
          val (ax, notify) = current.listeners.values.first()
          val xs = current.listeners.entries.drop(1)
          val update = WaitForTake(ax, xs.toMap())
          if (state.compareAndSet(current, update)) {
            notify(Result.success(Unit))
            Maybe.Just(current.value)
          } else {
            unsafeTryTake()
          }
        }
      }
      is WaitForPut -> Maybe.None
    }

  private tailrec suspend fun unsafeTake(onTake: Listener<A>): CancelToken =
    when (val current = state.value) {
      is WaitForTake -> {
        if (current.listeners.isEmpty()) {
          if (state.compareAndSet(current, State.empty())) {
            onTake(Result.success(current.value))
            CancelToken.unit
          } else {
            unsafeTake(onTake)
          }
        } else {
          val (ax, notify) = current.listeners.values.first()
          val xs = current.listeners.entries.drop(1)
          if (state.compareAndSet(current, WaitForTake(ax, xs.toMap()))) {
            notify(Result.success(Unit))
            onTake(Result.success(current.value))
            CancelToken.unit
          } else unsafeTake(onTake)
        }
      }
      is WaitForPut -> {
        val id = Token()
        val newQueue = current.takes + Pair(id, onTake)
        if (state.compareAndSet(current, WaitForPut(current.reads, newQueue))) CancelToken { unsafeCancelTake(id) }
        else unsafeTake(onTake)
      }
    }

  private tailrec fun unsafeCancelTake(id: Token): Unit =
    when (val current = state.value) {
      is WaitForPut -> {
        val newMap = current.takes - id
        val update = WaitForPut(current.reads, newMap)
        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelTake(id)
      }
      is WaitForTake -> Unit
    }

  private tailrec fun unsafeRead(onRead: Listener<A>): CancelToken =
    when (val current = state.value) {
      is WaitForTake -> {
        onRead(Result.success(current.value))
        CancelToken.unit
      }
      is WaitForPut -> {
        val id = Token()
        val newReads = current.reads + Pair(id, onRead)
        if (state.compareAndSet(current, WaitForPut(newReads, current.takes))) CancelToken { unsafeCancelRead(id) }
        else unsafeRead(onRead)
      }
    }

  private tailrec fun unsafeCancelRead(id: Token): Unit =
    when (val current = state.value) {
      is WaitForPut -> {
        val newMap = current.reads - id
        val update = WaitForPut(newMap, current.takes)
        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelRead(id)
      }
      is WaitForTake -> Unit
    }

  private suspend fun callPutAndAllReaders(
    a: A,
    put: Listener<A>?,
    reads: Map<Token, Listener<A>>
  ): Boolean {
    val value = Result.success(a)
    reads.values.callAll(value)

    return if (put != null) {
      put(value)
      true
    } else true
  }

  // For streaming a value to a whole `reads` collection
  private suspend fun Iterable<Listener<A>>.callAll(value: Result<A>): Unit =
    forEach { cb -> cb(value) }

  companion object {
    internal sealed class State<out A> {
      companion object {
        private val ref = WaitForPut<Any>(emptyMap(), emptyMap())
        operator fun <A> invoke(a: A): State<A> = WaitForTake(a, emptyMap())

        @Suppress("UNCHECKED_CAST")
        fun <A> empty(): State<A> = ref as State<A>
      }

      data class WaitForPut<A>(val reads: Map<Token, Listener<A>>, val takes: Map<Token, Listener<A>>) : State<A>()
      data class WaitForTake<A>(val value: A, val listeners: Map<Token, Pair<A, Listener<Unit>>>) : State<A>()
    }
  }
}

internal typealias Listener<A> = (Result<A>) -> Unit

internal fun <K, V> List<Map.Entry<K, V>>.toMap(): Map<K, V> =
  when (size) {
    0 -> emptyMap()
    else -> LinkedHashMap<K, V>(size).apply {
      for ((key, value) in this@toMap) {
        put(key, value)
      }
    }
  }
