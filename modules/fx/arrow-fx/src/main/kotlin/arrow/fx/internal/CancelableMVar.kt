package arrow.fx.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.Tuple2
import arrow.fx.CancelToken
import arrow.fx.MVar
import arrow.fx.internal.CancelableMVar.Companion.State.WaitForPut
import arrow.fx.internal.CancelableMVar.Companion.State.WaitForTake
import arrow.fx.typeclasses.Concurrent
import arrow.fx.typeclasses.Fiber
import arrow.fx.typeclasses.mapUnit
import arrow.fx.typeclasses.rightUnit
import kotlinx.atomicfu.atomic
import kotlin.coroutines.EmptyCoroutineContext

internal class CancelableMVar<F, A> private constructor(initial: State<A>, private val CF: Concurrent<F>) : MVar<F, A>, Concurrent<F> by CF {

  private val state = atomic(initial)

  companion object {
    /** Builds an [UncancelableMVar] instance with an [initial] value. */
    operator fun <F, A> invoke(initial: A, CF: Concurrent<F>): Kind<F, MVar<F, A>> = CF.later {
      CancelableMVar(State(initial), CF)
    }

    /** Returns an empty [UncancelableMVar] instance. */
    fun <F, A> empty(CF: Concurrent<F>): Kind<F, MVar<F, A>> = CF.later {
      CancelableMVar(State.empty<A>(), CF)
    }

    internal sealed class State<out A> {
      companion object {
        private val ref = WaitForPut<Any>(linkedMapOf(), linkedMapOf())
        operator fun <A> invoke(a: A): State<A> = WaitForTake(a, linkedMapOf())
        @Suppress("UNCHECKED_CAST")
        fun <A> empty(): State<A> = ref as State<A>
      }

      data class WaitForPut<A>(val reads: Map<Token, (Either<Nothing, A>) -> Unit>, val takes: Map<Token, (Either<Nothing, A>) -> Unit>) : State<A>()
      data class WaitForTake<A>(val value: A, val listeners: Map<Token, Tuple2<A, (Either<Nothing, Unit>) -> Unit>>) : State<A>()
    }
  }

  override fun isEmpty(): Kind<F, Boolean> = later {
    when (state.value) {
      is WaitForPut -> true
      is WaitForTake -> false
    }
  }

  override fun isNotEmpty(): Kind<F, Boolean> = later {
    when (state.value) {
      is WaitForPut -> false
      is WaitForTake -> true
    }
  }

  override fun put(a: A): Kind<F, Unit> =
    tryPut(a).flatMap { didPut ->
      if (didPut) unit() else cancelableF { cb -> unsafePut(a, cb) }
    }

  override fun tryPut(a: A): Kind<F, Boolean> =
    defer { unsafeTryPut(a) }

  override fun take(): Kind<F, A> =
    tryTake().flatMap {
      it.fold({ cancelableF(::unsafeTake) }, ::just)
    }

  override fun tryTake(): Kind<F, Option<A>> =
    defer { unsafeTryTake() }

  override fun read(): Kind<F, A> =
    cancelable(::unsafeRead)

  private tailrec fun unsafeTryPut(a: A): Kind<F, Boolean> =
    when (val current = state.value) {
      is State.WaitForTake -> just(false)
      is State.WaitForPut -> {
        val first = current.takes.values.firstOrNull()
        val update: State<A> = if (current.takes.isEmpty()) State(a) else {
          val rest = current.takes.toList().drop(1)
          if (rest.isEmpty()) State.empty()
          else State.WaitForPut(emptyMap(), rest.toMap())
        }

        if (!state.compareAndSet(current, update)) {
          unsafeTryPut(a)
        } else if (first != null || current.reads.isNotEmpty()) {
          callPutAndAllReaders(a, first, current.reads)
        } else just(true)
      }
    }

  private tailrec fun unsafePut(a: A, onPut: (Either<Nothing, Unit>) -> Unit): Kind<F, CancelToken<F>> =
    when (val current = state.value) {
      is State.WaitForTake -> {
        val id = Token()
        val newMap = current.listeners + Pair(id, Tuple2(a, onPut))
        if (state.compareAndSet(current, State.WaitForTake(current.value, newMap))) just(later { unsafeCancelPut(id) })
        else unsafePut(a, onPut)
      }
      is State.WaitForPut -> {
        val first = current.takes.values.firstOrNull()
        val update = if (current.takes.isEmpty()) State(a) else {
          val rest = current.takes.toList().drop(1)
          if (rest.isEmpty()) State.empty()
          else State.WaitForPut(emptyMap(), rest.toMap())
        }

        if (state.compareAndSet(current, update)) {
          if (first != null || current.reads.isNotEmpty()) callPutAndAllReaders(a, first, current.reads).map {
            onPut(rightUnit)
            unit()
          } else {
            onPut(rightUnit)
            just(unit())
          }
        } else unsafePut(a, onPut)
      }
    }

  private tailrec fun unsafeCancelPut(id: Token): Unit =
    when (val current = state.value) {
      is State.WaitForTake -> {
        val update = current.copy(listeners = current.listeners - id)
        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelPut(id)
      }
      is State.WaitForPut -> Unit
    }

  private tailrec fun unsafeTryTake(): Kind<F, Option<A>> =
    when (val current = state.value) {
      is State.WaitForTake -> {
        if (current.listeners.isEmpty()) {
          if (state.compareAndSet(current, State.empty())) just(Some(current.value))
          else unsafeTryTake()
        } else {
          val (ax, notify) = current.listeners.values.first()
          val xs = current.listeners.toList().drop(1)
          if (state.compareAndSet(current, State.WaitForTake(ax, xs.toMap()))) later { notify(rightUnit) }.fork(EmptyCoroutineContext).map { Some(current.value) }
          else unsafeTryTake()
        }
      }
      is State.WaitForPut -> just(None)
    }

  private tailrec fun unsafeTake(onTake: (Either<Nothing, A>) -> Unit): Kind<F, CancelToken<F>> =
    when (val current = state.value) {
      is State.WaitForTake -> {
        if (current.listeners.isEmpty()) {
          if (state.compareAndSet(current, State.empty())) {
            onTake(Right(current.value))
            just(unit())
          } else {
            unsafeTake(onTake)
          }
        } else {
          val (ax, notify) = current.listeners.values.first()
          val xs = current.listeners.toList().drop(0)
          if (state.compareAndSet(current, State.WaitForTake(ax, xs.toMap()))) {
            later { notify(rightUnit) }.fork(EmptyCoroutineContext).map {
              onTake(Right(current.value))
              unit()
            }
          } else unsafeTake(onTake)
        }
      }
      is State.WaitForPut -> {
        val id = Token()
        val newQueue = current.takes + Pair(id, onTake)
        if (state.compareAndSet(current, State.WaitForPut(current.reads, newQueue))) just(later { unsafeCancelTake(id) })
        else unsafeTake(onTake)
      }
    }

  private tailrec fun unsafeCancelTake(id: Token): Unit =
    when (val current = state.value) {
      is State.WaitForPut -> {
        val newMap = current.takes - id
        val update = State.WaitForPut(current.reads, newMap)
        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelTake(id)
      }
      is State.WaitForTake -> Unit
    }

  private tailrec fun unsafeRead(onRead: (Either<Nothing, A>) -> Unit): Kind<F, Unit> =
    when (val current = state.value) {
      is State.WaitForTake -> {
        onRead(Right(current.value))
        unit()
      }
      is State.WaitForPut -> {
        val id = Token()
        val newReads = current.reads + Pair(id, onRead)
        if (state.compareAndSet(current, State.WaitForPut(newReads, current.takes))) later { unsafeCancelRead(id) }
        else unsafeRead(onRead)
      }
    }

  private tailrec fun unsafeCancelRead(id: Token): Unit =
    when (val current = state.value) {
      is State.WaitForPut -> {
        val newMap = current.reads - id
        val update = State.WaitForPut(newMap, current.takes)
        if (state.compareAndSet(current, update)) Unit
        else unsafeCancelRead(id)
      }
      is State.WaitForTake -> Unit
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
}
