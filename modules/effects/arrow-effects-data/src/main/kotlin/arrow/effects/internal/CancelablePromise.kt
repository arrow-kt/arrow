package arrow.effects.internal

import arrow.Kind
import arrow.core.*
import arrow.effects.Promise
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.mapUnit
import java.util.concurrent.atomic.AtomicReference

internal class CancelablePromise<F, A>(CF: Concurrent<F>) : Promise<F, A>, Concurrent<F> by CF {

  internal sealed class State<out A> {
    data class Pending<A>(val joiners: Map<Token, (Either<Throwable, A>) -> Unit>) : State<A>()
    data class Full<A>(val value: A) : State<A>()
    data class Error<A>(val throwable: Throwable) : State<A>()
  }

  private val state: AtomicReference<State<A>> = AtomicReference(State.Pending(emptyMap()))

  override fun get(): Kind<F, A> = defer {
    when (val current = state.get()) {
      is State.Full -> just(current.value)
      is State.Pending -> cancelable { cb ->
        val id = unsafeRegister(cb)
        delay { unregister(id) }
      }
      is State.Error -> raiseError(current.throwable)
    }
  }

  override fun tryGet(): Kind<F, Option<A>> = delay {
    when (val current = state.get()) {
      is State.Full -> Some(current.value)
      is State.Pending -> None
      is State.Error -> None
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
    is State.Full -> just(false)
    is State.Error -> just(false)
    is State.Pending -> {
      if (state.compareAndSet(current, State.Full(a))) {
        val list = current.joiners.values
        if (list.isNotEmpty()) notify(a, list).map { true }
        else just(true)
      } else unsafeTryComplete(a)
    }
  }

  private tailrec fun unsafeTryError(error: Throwable): Kind<F, Boolean> = when (val current = state.get()) {
    is State.Full -> just(false)
    is State.Error -> just(false)
    is State.Pending -> {
      if (state.compareAndSet(current, State.Error(error))) {
        val list = current.joiners.values
        if (list.isNotEmpty()) notifyError(error, list).map { true }
        else just(true)
      } else unsafeTryError(error)
    }
  }

  private fun notify(a: A, list: Collection<(Either<Throwable, A>) -> Unit>): Kind<F, Unit> {
    val rightA = Right(a)

    return list.fold(unit()) { acc, next ->
      acc.flatMap { delay { next(rightA) }.startF(ImmediateContext).map(mapUnit) }
    }
  }

  private fun notifyError(error: Throwable, list: Collection<(Either<Throwable, A>) -> Unit>): Kind<F, Unit> {
    val leftError = Left(error)
    return list.fold(unit()) { acc, next ->
      acc.flatMap { delay { next(leftError) }.startF(ImmediateContext).map(mapUnit) }
    }
  }

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
    is State.Full -> Right(current.value)
    is State.Pending -> {
      val updated = State.Pending(current.joiners + Pair(id, cb))
      if (state.compareAndSet(current, updated)) null
      else register(id, cb)
    }
    is State.Error -> Left(current.throwable)
  }

  private tailrec fun unregister(id: Token): Unit = when (val current = state.get()) {
    is State.Full -> Unit
    is State.Error -> Unit
    is State.Pending -> {
      val updated = State.Pending(current.joiners - id)
      if (state.compareAndSet(current, updated)) Unit
      else unregister(id)
    }
  }

}