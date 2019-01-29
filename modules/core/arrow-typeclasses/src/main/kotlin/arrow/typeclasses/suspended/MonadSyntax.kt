package arrow.typeclasses.suspended

import arrow.Kind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

interface MonadSyntax<F> : ApplicativeSyntax<F>, Monad<F> {
  override fun <A> effect(fa: suspend () -> A): Kind<F, A> =
    BlockingContinuation<F, A>(this).apply {
      fa.startCoroutine(this)
    }.retVal
}

/**
 * A Blocking continuation ingests suspended functions from the environment and applies a continuation
 * to lift them into Kind<F, A>. For Monads unable to suspend effects blocking continuations are provided
 * which behave as expected depending whether the data type supports Monad, MonadThrow or the Async type classes.
 * In the case the datatype was able to support more powers such as async a different continuation style is
 */
internal open class BlockingContinuation<F, A>(AF: Applicative<F>) : Applicative<F> by AF, kotlin.coroutines.Continuation<A> {
  lateinit var retVal: Kind<F, A>
  override val context: CoroutineContext
    get() = EmptyCoroutineContext
  override fun resumeWith(result: Result<A>) {
    retVal = just(result.getOrThrow())
  }
}