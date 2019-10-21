package arrow.fx.internal

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.fx.Promise
import arrow.fx.typeclasses.Async
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

internal class UncancelablePromise<F, A>(private val AS: Async<F>) : Promise<F, A>, Async<F> by AS {

  internal sealed class State<out A> {
    data class Pending<A>(val joiners: List<(Either<Throwable, A>) -> Unit>) : State<A>()
    data class Complete<A>(val value: A) : State<A>()
    data class Error<A>(val throwable: Throwable) : State<A>()
  }

  private val state: AtomicRef<State<A>> = atomic(State.Pending(emptyList()))

  override fun get(): Kind<F, A> = async { k: (Either<Throwable, A>) -> Unit ->
    when (val result = register(k)) {
      null -> Unit
      is Either.Left -> k(result)
      is Either.Right -> k(result)
    }
  }

  private tailrec fun register(cb: (Either<Throwable, A>) -> Unit): Either<Throwable, A>? = when (val current = state.value) {
    is State.Complete -> Right(current.value)
    is State.Pending -> {
      val updated = State.Pending(current.joiners + cb)
      if (state.compareAndSet(current, updated)) null
      else register(cb)
    }
    is State.Error -> Left(current.throwable)
  }

  override fun tryGet(): Kind<F, Option<A>> =
    when (val oldState = state.value) {
      is State.Complete -> just(Some(oldState.value))
      is State.Pending<A> -> just(None)
      is State.Error -> just(None)
    }

  override fun complete(a: A): Kind<F, Unit> =
    tryComplete(a).flatMap { didComplete ->
      if (didComplete) just(Unit) else raiseError(Promise.AlreadyFulfilled)
    }

  override fun tryComplete(a: A): Kind<F, Boolean> = defer {
    unsafeTryComplete(a)
  }

  private tailrec fun unsafeTryComplete(a: A): Kind<F, Boolean> = when (val current = state.value) {
    is State.Complete -> just(false)
    is State.Error -> just(false)
    is State.Pending -> {
      if (state.compareAndSet(current, State.Complete(a))) {
        val list = current.joiners
        if (list.isNotEmpty()) {
          later {
            val result = Right(a)
            list.forEach { it(result) }
            true
          }
        } else just(true)
      } else unsafeTryComplete(a)
    }
  }

  override fun error(throwable: Throwable): Kind<F, Unit> =
    tryError(throwable).flatMap { didError ->
      if (didError) just(Unit) else raiseError(Promise.AlreadyFulfilled)
    }

  override fun tryError(throwable: Throwable): Kind<F, Boolean> =
    defer { unsafeTryError(throwable) }

  private tailrec fun unsafeTryError(error: Throwable): Kind<F, Boolean> = when (val current = state.value) {
    is State.Complete -> just(false)
    is State.Error -> just(false)
    is State.Pending -> {
      if (state.compareAndSet(current, State.Error(error))) {
        val list = current.joiners
        if (list.isNotEmpty()) {
          later {
            val result = Left(error)
            list.forEach { it(result) }
            true
          }
        } else just(true)
      } else unsafeTryError(error)
    }
  }

  override fun <A, B> Kind<F, A>.ap(ff: Kind<F, (A) -> B>): Kind<F, B> = AS.run {
    this@ap.ap(ff)
  }

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> = AS.run {
    this@map.map(f)
  }
}
