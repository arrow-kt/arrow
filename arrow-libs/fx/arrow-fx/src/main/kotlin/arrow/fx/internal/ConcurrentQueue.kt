package arrow.fx.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple2
import arrow.fx.Queue
import arrow.fx.Queue.BackpressureStrategy
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.rightUnit

internal class ConcurrentQueue<F, A> internal constructor(
  private val strategy: BackpressureStrategy,
  initial: State<F, A>?,
  private val CF: Concurrent<F>
) : Concurrent<F> by CF, Queue<F, A> {

  private val state: AtomicRefW<State<F, A>> = AtomicRefW(initial ?: State.empty())

  override fun size(): Kind<F, Int> = defer {
    when (val curr = state.value) {
      is State.Deficit -> just(-curr.takes.size)
      is State.Surplus -> just(1 + curr.offers.size)
    }
  }

  override fun tryOffer(a: A): Kind<F, Boolean> =
    defer { unsafeTryOffer(a) }

  override fun offer(a: A): Kind<F, Unit> =
    defer { unsafeTryOffer(a, false) }.flatMap { didPut ->
      if (didPut) unit() else cancellableF { cb -> unsafeOffer(a, cb) }
    }

  override fun offerAll(a: Iterable<A>): Kind<F, Unit> =
    defer { unsafeTryOfferAll(a, false) }.flatMap { didPut ->
      if (didPut) unit() else cancellableF { cb -> unsafeOfferAll(a, cb) }
    }

  override fun tryOfferAll(a: Iterable<A>): Kind<F, Boolean> =
    defer { unsafeTryOfferAll(a) }

  override fun tryTake(): Kind<F, Option<A>> =
    defer { unsafeTryTake() }

  override fun take(): Kind<F, A> =
    tryTake().flatMap {
      it.fold({ cancellableF(::unsafeTake) }, ::just)
    }

  override fun takeAll(): Kind<F, List<A>> =
    defer { unsafeTakeAll() }

  override fun tryPeek(): Kind<F, Option<A>> =
    defer { unsafeTryPeek() }

  override fun peek(): Kind<F, A> =
    tryPeek().flatMap {
      it.fold({ cancellableF(::unsafePeek) }, ::just)
    }

  override fun peekAll(): Kind<F, List<A>> =
    defer { unsafePeekAll() }

  private tailrec fun unsafeTryOffer(a: A, tryStrategy: Boolean = true): Kind<F, Boolean> =
    when (val current = state.value) {
      is State.Surplus ->
        unsafeOfferAllSurplusForStrategy(listOf(a), tryStrategy, current) ?: unsafeTryOffer(a, tryStrategy)

      is State.Deficit -> {
        val taker: Callback<A>? = current.takes.values.firstOrNull()

        val update: State<F, A> =
          if (taker == null) { // If no takers or values, we can safely store a single value in Surplus.
            State.Surplus(IQueue(a), emptyMap())
          } else { // Else we need to remove the first taker from the current state
            val rest = current.takes.entries.drop(1)
            if (rest.isEmpty()) current.copy(emptyMap(), emptyMap())
            else State.Deficit(emptyMap(), rest.toMap())
          }

        if (!state.compareAndSet(current, update)) {
          unsafeTryOffer(a, tryStrategy) // Update failed, recurse
        } else if (taker != null || current.peeks.isNotEmpty()) {
          callTakeAndPeeks(a, taker, current.peeks.values) // Update succeeded, call callbacks with value
        } else just(true) // Update succeeded, no callbacks need to be called.
      }
    }

  private tailrec fun unsafeTryOfferAll(aas: Iterable<A>, tryStrategy: Boolean = true): Kind<F, Boolean> =
    when (val current = state.value) {
      is State.Surplus ->
        unsafeOfferAllSurplusForStrategy(aas, tryStrategy, current) ?: unsafeTryOfferAll(aas, tryStrategy)

      is State.Deficit -> {
        if (aas.count() > current.takes.values.size) {
          unsafeOfferAllDecifitForStrategy(aas, tryStrategy, current) ?: unsafeTryOfferAll(aas, tryStrategy)
        } else {
          val update =
            if (aas.count() == current.takes.values.size) State.empty<F, A>()
            else current.copy(peeks = emptyMap(), takes = current.takes.entries.drop(aas.count()).toMap())

          if (!state.compareAndSet(current, update)) {
            unsafeTryOfferAll(aas, tryStrategy)
          } else if (current.takes.isNotEmpty() || current.peeks.isNotEmpty()) {
            streamAllPeeksAndTakers(aas, current.peeks.values, current.takes.values)
          } else just(true)
        }
      }
    }

  private tailrec fun unsafeOffer(a: A, onPut: Callback<Unit>): Kind<F, CancelToken<F>> =
    when (val current = state.value) {
      is State.Surplus -> {
        val id = Token()
        val newMap = current.offers + Pair(id, Tuple2(a, onPut))
        if (state.compareAndSet(current, State.Surplus(current.values, newMap))) just(later { unsafeCancelOffer(listOf(id)) })
        else unsafeOffer(a, onPut)
      }

      is State.Deficit -> {
        val first = current.takes.values.firstOrNull()
        val update: State<F, A> =
          if (current.takes.isEmpty()) {
            State.Surplus(IQueue(a), emptyMap())
          } else {
            val rest = current.takes.entries.drop(1)
            if (rest.isEmpty()) current.copy(emptyMap(), emptyMap())
            else State.Deficit(emptyMap(), rest.toMap())
          }

        if (state.compareAndSet(current, update)) {
          if (first != null || current.peeks.isNotEmpty()) callTakeAndPeeks(a, first, current.peeks.values).map {
            onPut(rightUnit)
            unit()
          } else {
            onPut(rightUnit)
            just(unit())
          }
        } else unsafeOffer(a, onPut)
      }
    }

  private tailrec fun unsafeOfferAll(a: Iterable<A>, onPut: Callback<Unit>): Kind<F, CancelToken<F>> =
    when (val current = state.value) {
      is State.Surplus -> {
        val tokens = (1..a.count()).map { Token() }
        val callbacks = (1..a.count()).map { noOpCallback }
        val tupled: List<Tuple2<A, Callback<Unit>>> = a.zip(callbacks, ::Tuple2)

        val id = Token()
        val lastOffer = Pair(id, Tuple2(a.last(), onPut)) // When last value is offered, than offerAll completes.
        val newOffers = tokens.zip(tupled, ::Pair) + lastOffer

        val newMap = current.offers + newOffers

        val update: State<F, A> = State.Surplus(current.values, newMap)

        if (state.compareAndSet(current, update)) just(later { unsafeCancelOffer(tokens + id) })
        else unsafeOfferAll(a, onPut)
      }

      is State.Deficit -> {
        val capacity = (strategy as? BackpressureStrategy.Bounded)?.capacity ?: 0
        if (a.count() > current.takes.size && (a.count() - current.takes.size) > capacity) {
          val takerValues = a.take(current.takes.size)
          val leftOver = a.drop(current.takes.size)
          val values = leftOver.take(capacity)
          val needOffered = leftOver.drop(capacity)

          val tokens = (1..needOffered.size).map { Token() }
          val callbacks = (1..needOffered.size).map { noOpCallback }
          val tupled: List<Tuple2<A, Callback<Unit>>> = needOffered.zip(callbacks, ::Tuple2)

          val id = Token()
          val lastOffer = Pair(id, Tuple2(needOffered.last(), onPut)) // When last value is offered, than offerAll completes.
          val newOffers = tokens.zip(tupled, ::Pair) + lastOffer

          val update = State.Surplus<F, A>(IQueue(values), newOffers.toMap())

          if (state.compareAndSet(current, update)) streamAllPeeksAndTakers(takerValues, current.peeks.values, current.takes.values).map {
            unit()
          } else unsafeOfferAll(a, onPut) // recurse
        } else {
          val update = State.Surplus<F, A>(IQueue(a.drop(current.takes.size)), emptyMap())

          if (state.compareAndSet(current, update)) streamAllPeeksAndTakers(a, current.peeks.values, current.takes.values).map {
            onPut(rightUnit)
            unit()
          } else unsafeOfferAll(a, onPut) // recurse
        }
      }
    }

  private tailrec fun unsafeCancelOffer(ids: List<Token>): Unit =
    when (val current = state.value) {
      is State.Surplus -> {
        val update = current.copy(offers = current.offers - ids)
        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelOffer(ids)
      }
      is State.Deficit -> Unit
    }

  private tailrec fun unsafeTryTake(): Kind<F, Option<A>> =
    when (val current = state.value) {
      is State.Surplus -> {
        val (head, tail) = current.values.dequeue()
        if (current.offers.isEmpty()) {
          val update: State<F, A> =
            if (tail.isEmpty()) State.empty<F, A>()
            else current.copy(values = tail)

          if (state.compareAndSet(current, update)) just(Some(head))
          else unsafeTryTake()
        } else {
          val (ax, notify) = current.offers.values.first()
          val xs = current.offers.entries.drop(1)
          val update: State<F, A> = State.Surplus(tail.enqueue(ax), xs.toMap())

          if (state.compareAndSet(current, update)) {
            later { notify(rightUnit) }.map { Some(head) } // Notify offer that it finished, and return value.
          } else unsafeTryTake() // compareAndSet failed try again
        }
      }
      is State.Deficit -> just(None)
    }

  private tailrec fun unsafeTake(onTake: Callback<A>): Kind<F, CancelToken<F>> =
    when (val current = state.value) {
      is State.Surplus -> {
        val (head, tail) = current.values.dequeue()
        if (current.offers.isEmpty()) {

          val update: State<F, A> =
            if (tail.isEmpty()) State.empty<F, A>()
            else current.copy(values = tail)

          if (state.compareAndSet(current, update)) {
            onTake(Right(head)) // Call takers callback with value
            just(unit())
          } else {
            unsafeTake(onTake) // Update failed, recurse
          }
        } else {
          val (ax, notify) = current.offers.values.first()
          val xs = current.offers.entries.drop(0)
          val update: State<F, A> = State.Surplus(tail.enqueue(ax), xs.toMap())

          if (state.compareAndSet(current, update)) {
            later { notify(rightUnit) }.map { // Notify offer that it finished,
              onTake(Either.Right(head)) // Call takers callback with value
              unit()
            }
          } else unsafeTake(onTake) // Update failed, recurse
        }
      }

      is State.Deficit -> {
        val id = Token()
        val newQueue = current.takes + Pair(id, onTake)
        val update: State<F, A> = State.Deficit(current.peeks, newQueue)
        if (state.compareAndSet(current, update)) just(later { unsafeCancelTake(id) })
        else unsafeTake(onTake)
      }
    }

  private tailrec fun unsafeCancelTake(id: Token): Unit =
    when (val current = state.value) {
      is State.Deficit -> {
        val newMap = current.takes - id
        val update = State.Deficit<F, A>(current.peeks, newMap)
        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelTake(id)
      }
      is State.Surplus -> Unit
    }

  private tailrec fun unsafeTakeAll(): Kind<F, List<A>> =
    when (val current = state.value) {
      is State.Surplus -> {
        val all = current.values.toList()
        if (current.offers.isEmpty()) {
          val update: State<F, A> = State.empty<F, A>()

          if (state.compareAndSet(current, update)) just(all)
          else unsafeTakeAll()
        } else {
          val allValues = current.offers.values.map { it.a }
          val allOffers = current.offers.values.map { it.b }
          val update = State.empty<F, A>()

          // Call all outstanding offer calls that they're  finished, and return all available + offered values.
          if (state.compareAndSet(current, update)) later { allOffers.forEach { cb -> cb(rightUnit) } }.map { all + allValues }
          else unsafeTakeAll()
        }
      }
      is State.Deficit -> just(emptyList())
    }

  private fun unsafePeekAll(): Kind<F, List<A>> =
    when (val current = state.value) {
      is State.Deficit -> just(emptyList())
      is State.Surplus -> {
        val all = current.values.toList()
        val allOffered = current.offers.values.map { it.a }
        just(all + allOffered)
      }
    }

  private fun unsafeTryPeek(): Kind<F, Option<A>> =
    when (val current = state.value) {
      is State.Surplus -> just(Some(current.values.head()))
      is State.Deficit -> just(None)
    }

  private tailrec fun unsafePeek(onPeek: Callback<A>): Kind<F, CancelToken<F>> =
    when (val current = state.value) {
      is State.Surplus -> {
        onPeek(Right(current.values.head()))
        just(unit())
      }
      is State.Deficit -> {
        val id = Token()
        val newReads = current.peeks + Pair(id, onPeek)
        val update: State<F, A> = State.Deficit(newReads, current.takes)

        if (state.compareAndSet(current, update)) just(later { unsafeCancelRead(id) })
        else unsafePeek(onPeek)
      }
    }

  private tailrec fun unsafeCancelRead(id: Token): Unit =
    when (val current = state.value) {
      is State.Deficit -> {
        val newMap = current.peeks - id
        val update: State<F, A> = State.Deficit(newMap, current.takes)

        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelRead(id)
      }
      is State.Surplus -> Unit
    }

  /**
   * A Queue can be in three states
   * [Deficit]: Contains three maps of registered id & take/reads/shutdown callbacks waiting
   *   for a value to become available.
   *
   * [Surplus]: Contains a queue of values and two maps of registered id & offer/shutdown callbacks waiting to
   *   offer once there is room (if the queue is bounded, dropping or sliding).
   */
  sealed class State<F, out A> {
    data class Deficit<F, A>(
      val peeks: Map<Token, Callback<A>>,
      val takes: Map<Token, Callback<A>>
    ) : State<F, A>()

    data class Surplus<F, A>(
      val values: IQueue<A>,
      val offers: Map<Token, Tuple2<A, Callback<Unit>>>
    ) : State<F, A>()

    companion object {
      private val empty: Deficit<Any?, Any?> = Deficit(emptyMap(), emptyMap())
      fun <F, A> empty(): Deficit<F, A> = empty as Deficit<F, A>
    }
  }

  /**
   * Unsafely handle offer at State.Surplus.
   *
   * Return:
   *  - `just(true)` when handled
   *  - `just(false)` when offer should be scheduled (only used for bounded).
   *     or when [tryStrategy] is true to signal no room in the [Queue].
   *  - null when needs to recurse and try again
   */
  private fun unsafeOfferAllSurplusForStrategy(a: Iterable<A>, tryStrategy: Boolean, surplus: State.Surplus<F, A>): Kind<F, Boolean>? =
    when (strategy) {
      is BackpressureStrategy.Bounded ->
        when {
          surplus.values.length() + a.count() > strategy.capacity -> just(false)
          state.compareAndSet(surplus, surplus.copy(surplus.values.enqueue(a))) -> just(true)
          else -> null
        }
      is BackpressureStrategy.Sliding -> {
        val nextQueue = if (surplus.values.length() + (a.count() - 1) < strategy.capacity) surplus.values.enqueue(a)
        else surplus.values.drop(a.count()).enqueue(a)

        when {
          surplus.values.length() >= strategy.capacity && tryStrategy -> just(false)
          state.compareAndSet(surplus, surplus.copy(values = nextQueue)) -> just(true)
          else -> null
        }
      }
      is BackpressureStrategy.Dropping -> {
        val nextQueue = if (surplus.values.length() + (a.count() - 1) < strategy.capacity) surplus.values.enqueue(a)
        else surplus.values

        when {
          surplus.values.length() >= strategy.capacity && tryStrategy -> just(false)
          state.compareAndSet(surplus, surplus.copy(values = nextQueue)) -> just(true)
          else -> null
        }
      }
      is BackpressureStrategy.Unbounded ->
        if (!state.compareAndSet(surplus, surplus.copy(values = surplus.values.enqueue(a)))) null
        else just(true)
    }

  private fun unsafeOfferAllDecifitForStrategy(a: Iterable<A>, tryStrategy: Boolean, deficit: State.Deficit<F, A>): Kind<F, Boolean>? =
    when (strategy) {
      is BackpressureStrategy.Bounded ->
        when {
          // We need to atomically offer to deficit AND schedule offers according to capacity.
          a.count() > deficit.takes.size && (a.count() - deficit.takes.size) > strategy.capacity -> just(false)

          a.count() > deficit.takes.size -> { // Offer to deficit and update to Surplus, capacity check above
            val update = State.Surplus<F, A>(IQueue(a.drop(deficit.takes.size)), emptyMap())

            if (state.compareAndSet(deficit, update)) streamAllPeeksAndTakers(a, deficit.peeks.values, deficit.takes.values)
            else null // recurse
          }
          else -> null // updates failed, recurse
        }
      is BackpressureStrategy.Sliding -> {
        val afterTakers = a.count() - deficit.takes.size
        if (afterTakers > strategy.capacity && tryStrategy) just(false)
        else {
          val newQueue = if (afterTakers > strategy.capacity) IQueue(a.drop(deficit.takes.size).drop(afterTakers - strategy.capacity))
          else IQueue(a.drop(deficit.takes.size))
          val update = State.Surplus<F, A>(newQueue, emptyMap())

          if (state.compareAndSet(deficit, update)) streamAllPeeksAndTakers(a, deficit.peeks.values, deficit.takes.values)
          else null // recurse
        }
      }
      is BackpressureStrategy.Dropping -> {
        val afterTakers = a.count() - deficit.takes.size
        if (afterTakers > strategy.capacity && tryStrategy) just(false)
        else {
          val newQueue = if (afterTakers > strategy.capacity) IQueue(a.drop(deficit.takes.size).take(strategy.capacity))
          else IQueue(a.drop(deficit.takes.size))

          val update = State.Surplus<F, A>(newQueue, emptyMap())
          if (state.compareAndSet(deficit, update)) streamAllPeeksAndTakers(a, deficit.peeks.values, deficit.takes.values)
          else null // recurse
        }
      }
      is BackpressureStrategy.Unbounded -> {
        val update = State.Surplus<F, A>(IQueue(a.drop(deficit.takes.size)), emptyMap())
        if (state.compareAndSet(deficit, update)) streamAllPeeksAndTakers(a, deficit.peeks.values, deficit.takes.values)
        else null
      }
    }

  private fun streamAllPeeksAndTakers(a: Iterable<A>, peeks: Iterable<Callback<A>>, takes: Iterable<Callback<A>>): Kind<F, Boolean> =
    later {
      a.firstOrNull()?.let { Either.Right(it) }?.let { a ->
        peeks.forEach { cb -> cb(a) }
      }

      a.zip(takes.take(a.count())) { a, cb ->
        cb(Either.Right(a))
      }

      true
    }

  private fun callTakeAndPeeks(a: A, take: Callback<A>?, peeks: Iterable<Callback<A>>): Kind<F, Boolean> {
    val value = Right(a)
    return peeks.callAll(value).flatMap {
      if (take != null) later { take(value) }.map { true }
      else just(true)
    }
  }

  // For streaming a value to a whole `reads` collection
  private fun Iterable<Callback<A>>.callAll(value: Either<Nothing, A>): Kind<F, Unit> =
    later { forEach { cb -> cb(value) } }

  private val noOpCallback: Callback<Unit> =
    { _: Either<Throwable, Unit> -> Unit }

  companion object {
    fun <F, A> empty(CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.later {
      ConcurrentQueue<F, A>(BackpressureStrategy.Unbounded, null, CF)
    }
  }
}

internal fun <K, V> List<Map.Entry<K, V>>.toMap(): Map<K, V> =
  when (size) {
    0 -> emptyMap()
    else -> LinkedHashMap<K, V>(size).apply {
      for ((key, value) in this@toMap) {
        put(key, value)
      }
    }
  }

internal typealias Callback<A> = (Either<Throwable, A>) -> Unit
