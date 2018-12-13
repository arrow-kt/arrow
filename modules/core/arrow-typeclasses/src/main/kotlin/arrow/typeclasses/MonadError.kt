package arrow.typeclasses

import arrow.Kind
import kotlin.coroutines.startCoroutine

/**
 * ank_macro_hierarchy(arrow.typeclasses.MonadError)
 */
interface MonadError<F, E> : ApplicativeError<F, E>, Monad<F> {

  fun <A> Kind<F, A>.ensure(error: () -> E, predicate: (A) -> Boolean): Kind<F, A> =
    this.flatMap {
      if (predicate(it)) just(it)
      else raiseError(error())
    }

}

interface MonadThrow<F> : MonadError<F, Throwable> {

  /**
   * Entry point for monad bindings which enables for comprehensions. The underlying implementation is based on coroutines.
   * A coroutine is initiated and suspended inside [MonadErrorContinuation] yielding to [Monad.flatMap]. Once all the flatMap binds are completed
   * the underlying monad is returned from the act of executing the coroutine
   *
   * This one operates over [MonadError] instances that can support [Throwable] in their error type automatically lifting
   * errors as failed computations in their monadic context and not letting exceptions thrown as the regular monad binding does.
   */
  fun <B> bindingCatch(c: suspend MonadErrorContinuation<F, *>.() -> B): Kind<F, B> {
    val continuation = MonadErrorContinuation<F, B>(this)
    val wrapReturn: suspend MonadErrorContinuation<F, *>.() -> Kind<F, B> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }

}

