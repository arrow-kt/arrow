package arrow.fx.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple2
import arrow.core.internal.AtomicRefW
import arrow.core.left

import arrow.fx.Queue
import arrow.fx.QueueShutdown
import arrow.fx.typeclasses.CancelToken
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.mapUnit
import arrow.fx.typeclasses.rightUnit
import arrow.typeclasses.Applicative
import kotlin.coroutines.EmptyCoroutineContext

class CancelableQueue<F, A> internal constructor(
  initial: State<F, A>?,
  private val strategy: SurplusStrategy<F, A>,
  private val CF: Concurrent<F>
) : Concurrent<F> by CF, Queue<F, A> {

  private val state: AtomicRefW<State<F, A>> = AtomicRefW(initial ?: State.empty())

  companion object {
    operator fun <F, A> invoke(initial: List<A>, CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.later {
      CancelableQueue<F, A>(State.Surplus(IQueue(initial), linkedMapOf(), linkedMapOf()), SurplusStrategy.Unbounded(CF), CF)
    }

    /** Returns an empty [UncancelableMVar] instance. */
    fun <F, A> empty(CF: Concurrent<F>): Kind<F, Queue<F, A>> = CF.later {
      CancelableQueue<F, A>(null, SurplusStrategy.Unbounded(CF), CF)
    }

    internal sealed class State<F, out A> {
      data class Deficit<F, A>(
        val reads: Map<Token, (Either<Throwable, A>) -> Unit>,
        val takes: Map<Token, (Either<Throwable, A>) -> Unit>,
        val shutdownHook: Map<Token, (Either<Throwable, Unit>) -> Unit>
      ) : State<F, A>()

      data class Surplus<F, A>(
        val value: IQueue<A>,
        val offers: Map<Token, Tuple2<A, (Either<Throwable, Unit>) -> Unit>>,
        val shutdownHook: Map<Token, (Either<Throwable, Unit>) -> Unit>
      ) : State<F, A>()

      object Shutdown : State<Any?, Nothing>()

      companion object {
        private val empty: State.Deficit<Any?, Any?> = State.Deficit(linkedMapOf(), linkedMapOf(), linkedMapOf())
        fun <F, A> empty(): State.Deficit<F, A> = empty as State.Deficit<F, A>
        fun <F, A> shutdown(): State<F, A> =
          Shutdown as State<F, A>
      }
    }
  }

  override fun offer(a: A): Kind<F, Unit> =
    tryOffer(a).flatMap { didPut ->
      if (didPut) unit() else cancelableF { cb -> unsafeOffer(a, cb) }
    }

  override fun shutdown(): Kind<F, Unit> =
    defer { unsafeShutdown() }

  override fun size(): Kind<F, Int> = defer {
    when (val curr = state.value) {
      is State.Deficit -> just(-curr.takes.size)
      is State.Surplus -> just(1 + curr.offers.size)
      State.Shutdown -> raiseError(QueueShutdown)
    }
  }

  fun isEmpty(): Kind<F, Boolean> =
    size().map { it == 0 }

  fun isNotEmpty(): Kind<F, Boolean> =
    isEmpty().map(Boolean::not)

  override fun awaitShutdown(): Kind<F, Unit> =
    CF.cancelableF(::unsafeRegisterAwaitShutdown)

  fun tryOffer(a: A): Kind<F, Boolean> =
    defer { unsafeTryOffer(a) }

  override fun take(): Kind<F, A> =
    tryTake().flatMap {
      it.fold({ cancelableF(::unsafeTake) }, ::just)
    }

  fun tryTake(): Kind<F, Option<A>> =
    defer { unsafeTryTake() }

  fun read(): Kind<F, A> =
    cancelable(::unsafeRead)

  private tailrec fun unsafeTryOffer(a: A): Kind<F, Boolean> =
    when (val current = state.value) {
      is State.Surplus -> strategy.unsafeTryHandleSurplus(state, a, current) ?: unsafeTryOffer(a)
      is State.Deficit -> {
        val firstTake = current.takes.values.firstOrNull()

        val update: State<F, A> = if (firstTake == null) State.Surplus(IQueue(a), linkedMapOf(), current.shutdownHook) else {
          val rest = current.takes.toList().drop(1)
          if (rest.isEmpty()) current.copy(linkedMapOf(), linkedMapOf())
          else State.Deficit(emptyMap(), rest.toMap(), current.shutdownHook)
        }

        if (!state.compareAndSet(current, update)) {
          unsafeTryOffer(a)
        } else if (firstTake != null || current.reads.isNotEmpty()) {
          callPutAndAllReaders(a, firstTake, current.reads)
        } else just(true)
      }
      is State.Shutdown -> raiseError(QueueShutdown)
    }

  private tailrec fun unsafeRegisterAwaitShutdown(shutdown: (Either<Throwable, Unit>) -> Unit): Kind<F, CancelToken<F>> =
    when (val curr = state.value) {
      is State.Deficit -> {
        val token = Token()
        val newShutdowns = curr.shutdownHook + Pair(token, shutdown)
        if (state.compareAndSet(curr, curr.copy(shutdownHook = newShutdowns))) just(later { unsafeCancelAwaitShutdown(token) })
        else unsafeRegisterAwaitShutdown(shutdown)
      }
      is State.Surplus -> {
        val token = Token()
        val newShutdowns = curr.shutdownHook + Pair(token, shutdown)
        if (state.compareAndSet(curr, curr.copy(shutdownHook = newShutdowns))) just(later { unsafeCancelAwaitShutdown(token) })
        else unsafeRegisterAwaitShutdown(shutdown)
      }
      State.Shutdown -> {
        shutdown(rightUnit)
        just(CF.unit())
      }
    }

  private tailrec fun unsafeOffer(a: A, onPut: (Either<Throwable, Unit>) -> Unit): Kind<F, CancelToken<F>> =
    when (val current = state.value) {
      is State.Surplus -> {
        val id = Token()
        val newMap = current.offers + Pair(id, Tuple2(a, onPut))
        if (state.compareAndSet(current, State.Surplus(current.value, newMap, current.shutdownHook))) just(later { unsafeCancelOffer(id) })
        else unsafeOffer(a, onPut)
      }
      is State.Deficit -> {
        val first = current.takes.values.firstOrNull()
        val update = if (current.takes.isEmpty()) State.Surplus<F, A>(IQueue(a), linkedMapOf(), current.shutdownHook) else {
          val rest = current.takes.toList().drop(1)
          if (rest.isEmpty()) current.copy(linkedMapOf(), linkedMapOf())
          else State.Deficit(emptyMap(), rest.toMap(), current.shutdownHook)
        }

        if (state.compareAndSet(current, update)) {
          if (first != null || current.reads.isNotEmpty()) callPutAndAllReaders(a, first, current.reads).map {
            onPut(rightUnit)
            unit()
          } else {
            onPut(rightUnit)
            just(unit())
          }
        } else unsafeOffer(a, onPut)
      }
      is State.Shutdown -> {
        onPut(Either.Left(QueueShutdown))
        just(unit())
      }
    }

  private tailrec fun unsafeShutdown(): Kind<F, Unit> =
    when (val current = state.value) {
      is State.Shutdown -> raiseError(QueueShutdown)
      is State.Surplus ->
        if (state.compareAndSet(current, State.shutdown())) later {
          current.offers.values.forEach { (_, cb) -> cb(QueueShutdown.left()) }
          current.shutdownHook.values.forEach { cb -> cb(rightUnit) }
        } else unsafeShutdown()
      is State.Deficit ->
        if (state.compareAndSet(current, State.shutdown())) later {
          current.takes.forEach { (_, cb) -> cb(QueueShutdown.left()) }
          current.reads.forEach { (_, cb) -> cb(QueueShutdown.left()) }
          current.shutdownHook.values.forEach { cb -> cb(rightUnit) }
        } else unsafeShutdown()
    }

  private tailrec fun unsafeCancelAwaitShutdown(id: Token): Unit =
    when (val curr = state.value) {
      is State.Deficit -> {
        val update = curr.copy(shutdownHook = curr.shutdownHook - id)
        if (state.compareAndSet(curr, update)) Unit
        else unsafeCancelAwaitShutdown(id)
      }
      is State.Surplus -> {
        val update = curr.copy(shutdownHook = curr.shutdownHook - id)
        if (state.compareAndSet(curr, update)) Unit
        else unsafeCancelAwaitShutdown(id)
      }
      else -> Unit
    }

  private tailrec fun unsafeCancelOffer(id: Token): Unit =
    when (val current = state.value) {
      is State.Surplus -> {
        val update = current.copy(offers = current.offers - id)
        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelOffer(id)
      }
      else -> Unit
    }

  private tailrec fun unsafeTryTake(): Kind<F, Option<A>> =
    when (val current = state.value) {
      is State.Surplus -> {
        val (head, tail) = current.value.dequeue()
        if (current.offers.isEmpty()) {
          val update = if (tail.isEmpty()) State.empty<F, A>().copy(shutdownHook = current.shutdownHook) else current.copy(value = tail)
          if (state.compareAndSet(current, update)) just(Some(head))
          else unsafeTryTake()
        } else {
          val (ax, notify) = current.offers.values.first()
          val xs = current.offers.toList().drop(1)
          // TODO ADD STRATEGY
          if (state.compareAndSet(current, State.Surplus<F, A>(tail.enqueue(ax), xs.toMap(), current.shutdownHook))) later { notify(rightUnit) }.fork(EmptyCoroutineContext).map { Some(head) }
          else unsafeTryTake()
        }
      }
      is State.Deficit -> just(None)
      is State.Shutdown -> just(None)
    }

  private tailrec fun unsafeTake(onTake: (Either<Throwable, A>) -> Unit): Kind<F, CancelToken<F>> =
    when (val current = state.value) {
      is State.Surplus -> {
        val (head, tail) = current.value.dequeue()
        if (current.offers.isEmpty()) {
          val update = if (tail.isEmpty()) State.empty<F, A>().copy(shutdownHook = current.shutdownHook) else current.copy(value = tail)
          if (state.compareAndSet(current, update)) {
            onTake(Right(head))
            just(unit())
          } else {
            unsafeTake(onTake)
          }
        } else {
          val (ax, notify) = current.offers.values.first()
          val xs = current.offers.toList().drop(0) // TODO ADD STRATEGY
          if (state.compareAndSet(current, State.Surplus(tail.enqueue(ax), xs.toMap(), current.shutdownHook))) {
            later { notify(rightUnit) }.fork(EmptyCoroutineContext).map {
              onTake(Either.Right(head))
              unit()
            }
          } else unsafeTake(onTake)
        }
      }
      is State.Deficit -> {
        val id = Token()
        val newQueue = current.takes + Pair(id, onTake)
        if (state.compareAndSet(current, State.Deficit(current.reads, newQueue, current.shutdownHook))) just(later { unsafeCancelTake(id) })
        else unsafeTake(onTake)
      }
      is State.Shutdown -> {
        onTake(Either.Left(QueueShutdown))
        just(unit())
      }
    }

  private tailrec fun unsafeCancelTake(id: Token): Unit =
    when (val current = state.value) {
      is State.Deficit -> {
        val newMap = current.takes - id
        val update = State.Deficit<F, A>(current.reads, newMap, current.shutdownHook)
        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelTake(id)
      }
      else -> Unit
    }

  private tailrec fun unsafeRead(onRead: (Either<Throwable, A>) -> Unit): Kind<F, Unit> =
    when (val current = state.value) {
      is State.Surplus -> {
        onRead(Right(current.value.head()))
        unit()
      }
      is State.Deficit -> {
        val id = Token()
        val newReads = current.reads + Pair(id, onRead)
        if (state.compareAndSet(current, State.Deficit(newReads, current.takes, current.shutdownHook))) later { unsafeCancelRead(id) }
        else unsafeRead(onRead)
      }
      is State.Shutdown -> raiseError(QueueShutdown)
    }

  private tailrec fun unsafeCancelRead(id: Token): Unit =
    when (val current = state.value) {
      is State.Deficit -> {
        val newMap = current.reads - id
        val update = State.Deficit<F, A>(newMap, current.takes, current.shutdownHook)
        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelRead(id)
      }
      else -> Unit
    }

  private fun callPutAndAllReaders(a: A, put: ((Either<Nothing, A>) -> Unit)?, reads: Map<Token, (Either<Nothing, A>) -> Unit>): Kind<F, Boolean> {
    val value = Right(a)
    return reads.values.callAll(value).flatMap {
      if (put != null) later { put(value) }.fork(EmptyCoroutineContext).map { true }
      else just(true)
    }
  }

  // For streaming a value to a whole `reads` collection
  private fun Iterable<(Either<Nothing, A>) -> Unit>.callAll(value: Either<Nothing, A>): Kind<F, Unit> =
    fold(null as Kind<F, Fiber<F, Unit>>?) { acc, cb ->
      val task = later { cb(value) }.fork(EmptyCoroutineContext)
      acc?.flatMap { task } ?: task
    }?.map(mapUnit) ?: unit()

  override fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B> = CF.run {
    this@ap.ap(ff)
  }

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> = CF.run {
    this@map.map(f)
  }

  internal sealed class SurplusStrategy<F, A> {
    abstract fun unsafeTryHandleSurplus(state: AtomicRefW<State<F, A>>, a: A, surplus: State.Surplus<F, A>): Kind<F, Boolean>?

    data class Bounded<F, A>(val capacity: Int, val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun unsafeTryHandleSurplus(state: AtomicRefW<State<F, A>>, a: A, surplus: State.Surplus<F, A>): Kind<F, Boolean>? = when {
        surplus.value.length() >= capacity -> AP.just(false)
        state.compareAndSet(surplus, surplus.copy(surplus.value.enqueue(a))) -> AP.just(true)
        else -> null
      }
    }

    data class Sliding<F, A>(val capacity: Int, val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun unsafeTryHandleSurplus(state: AtomicRefW<State<F, A>>, a: A, surplus: State.Surplus<F, A>): Kind<F, Boolean>? {
        val nextQueue = if (surplus.value.length() < capacity) surplus.value.enqueue(a) else surplus.value.dequeue().b.enqueue(a)
        return if (!state.compareAndSet(surplus, surplus.copy(value = nextQueue))) null
        else AP.just(true)
      }
    }

    data class Dropping<F, A>(val capacity: Int, val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun unsafeTryHandleSurplus(state: AtomicRefW<State<F, A>>, a: A, surplus: State.Surplus<F, A>): Kind<F, Boolean>? {
        val nextQueue = if (surplus.value.length() < capacity) surplus.value.enqueue(a) else surplus.value
        return if (!state.compareAndSet(surplus, surplus.copy(value = nextQueue))) null
        else AP.just(true)
      }
    }

    data class Unbounded<F, A>(val AP: Applicative<F>) : SurplusStrategy<F, A>() {
      override fun unsafeTryHandleSurplus(state: AtomicRefW<State<F, A>>, a: A, surplus: State.Surplus<F, A>): Kind<F, Boolean>? =
        if (!state.compareAndSet(surplus, surplus.copy(value = surplus.value.enqueue(a)))) null
        else AP.just(true)
    }
  }
}
