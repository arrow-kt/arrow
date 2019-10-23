package arrow.fx.internal

import arrow.Kind
import arrow.core.*
import arrow.fx.Promise
import arrow.fx.internal.CancelablePromise.State.*
import arrow.fx.typeclasses.*
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.EmptyCoroutineContext

internal class CancelablePromise<F, A>(private val CF: Concurrent<F, Throwable>) : Promise<F, A>, Concurrent<F, Throwable> by CF {

  internal sealed class State<out A> {
    data class Pending<A>(val joiners: Map<Token, (Either<Throwable, A>) -> Unit>) : State<A>()
    data class Complete<A>(val value: A) : State<A>()
    data class Error<A>(val throwable: Throwable) : State<A>()
  }

  private val state: AtomicReference<State<A>> = AtomicReference(Pending(emptyMap()))

  override fun get(): Kind<F, A> = defer(throwPolicy) {
    when (val current = state.get()) {
      is Complete -> just(current.value)
      is Pending -> cancelable { cb ->
        val id = unsafeRegister(cb)
        later { unregister(id) }
      }
      is Error -> raiseError(current.throwable)
    }
  }

  override fun tryGet(): Kind<F, Option<A>> = later {
    when (val current = state.get()) {
      is Complete -> Some(current.value)
      is Pending -> None
      is Error -> None
    }
  }

  override fun complete(a: A): Kind<F, Unit> =
    tryComplete(a).flatMap { didComplete ->
      if (didComplete) just(Unit)
      else raiseError(Promise.AlreadyFulfilled)
    }

  override fun tryComplete(a: A): Kind<F, Boolean> =
    defer { unsafeTryComplete(a) }

  override fun error(throwable: Throwable): Kind<F, Unit> =
    tryError(throwable).flatMap { didError ->
      if (didError) just(Unit)
      else raiseError(Promise.AlreadyFulfilled)
    }

  override fun tryError(throwable: Throwable): Kind<F, Boolean> =
    defer { unsafeTryError(throwable) }

  private tailrec fun unsafeTryComplete(a: A): Kind<F, Boolean> = when (val current = state.get()) {
    is Complete -> just(false)
    is Error -> just(false)
    is Pending -> {
      if (state.compareAndSet(current, Complete(a))) {
        val joiners = current.joiners.values
        if (joiners.isNotEmpty()) joiners.callAll(Right(a)).map { true }
        else just(true)
      } else unsafeTryComplete(a)
    }
  }

  private tailrec fun unsafeTryError(error: Throwable): Kind<F, Boolean> = when (val current = state.get()) {
    is Complete -> just(false)
    is Error -> just(false)
    is Pending -> {
      if (state.compareAndSet(current, Error(error))) {
        val joiners = current.joiners.values
        if (joiners.isNotEmpty()) joiners.callAll(Left(error)).map { true }
        else just(true)
      } else unsafeTryError(error)
    }
  }

  private fun Iterable<(Either<Throwable, A>) -> Unit>.callAll(value: Either<Throwable, A>): Kind<F, Unit> =
    fold(null as Kind<F, Fiber<F, Unit>>?) { acc, cb ->
      val task = EmptyCoroutineContext.startFiber(later { cb(value) })
      acc?.flatMap { task } ?: task
    }?.map(mapToUnit) ?: unit()

  private fun unsafeRegister(cb: (Either<Throwable, A>) -> Unit): Token {
    val id = Token()
    when (val result = register(id, cb)) {
      null -> Unit
      is Either.Left -> cb(result)
      is Either.Right -> cb(result)
    }
    return id
  }

  private tailrec fun register(id: Token, cb: (Either<Throwable, A>) -> Unit): Either<Throwable, A>? = when (val current = state.get()) {
    is Complete -> Right(current.value)
    is Pending -> {
      val updated = Pending(current.joiners + Pair(id, cb))
      if (state.compareAndSet(current, updated)) null
      else register(id, cb)
    }
    is Error -> Left(current.throwable)
  }

  private tailrec fun unregister(id: Token): Unit = when (val current = state.get()) {
    is Complete -> Unit
    is Error -> Unit
    is Pending -> {
      val updated = Pending(current.joiners - id)
      if (state.compareAndSet(current, updated)) Unit
      else unregister(id)
    }
  }

  override fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B> = CF.run {
    this@ap.ap(ff)
  }

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> = CF.run {
    this@map.map(f)
  }
}
