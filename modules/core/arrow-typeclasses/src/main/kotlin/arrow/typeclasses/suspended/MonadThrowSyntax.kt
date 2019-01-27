package arrow.typeclasses.suspended

import arrow.Kind
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadThrow
import kotlin.coroutines.startCoroutine

interface MonadThrowSyntax<F> : MonadErrorSyntax<F, Throwable>, MonadThrow<F> {
  override fun <A> f(fa: suspend () -> A): Kind<F, A> =
    BlockingErrorContinuation<F, A>(this).apply {
      fa.startCoroutine(this)
    }.retVal
}

/**
 * A Blocking continuation ingests suspended functions from the environment and applies a continuation
 * to lift them into Kind<F, A>. For Monads unable to suspend effects blocking continuations are provided
 * which behave as expected depending whether the data type supports Monad, MonadThrow or the Async type classes.
 * In the case the datatype was able to support more powers such as async a different continuation style is
 */
internal open class BlockingErrorContinuation<F, A>(AF: ApplicativeError<F, Throwable>) :
  BlockingContinuation<F, A>(AF),
  ApplicativeError<F, Throwable> by AF {
  override fun resumeWith(result: Result<A>) {
    retVal = result.fold(::just, ::raiseError)
  }
}