package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.effects.typeclasses.Async
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

interface AsyncSyntax<F> : MonadDeferSyntax<F>, Async<F> {

  override fun <A> effect(fa: suspend () -> A): Kind<F, A> =
    super.async { cb ->
      AsyncContinuation(cb).apply {
        fa.startCoroutine(this)
      }
    }

  private fun <A> asyncOp(fb: Async<F>.() -> Kind<F, A>): Kind<F, A> =
    run<Async<F>, Kind<F, A>> { fb(this) }

  fun <A> CoroutineContext.effect(f: suspend () -> A): Kind<F, A> =
    asyncOp { defer(this@effect) { f.effect() } }

}

internal open class AsyncContinuation<A>(val cb: (Either<Throwable, A>) -> Unit) : kotlin.coroutines.Continuation<A> {
  override val context: CoroutineContext
    get() = EmptyCoroutineContext // TODO this should probably use the default dispatcher by default.
  override fun resumeWith(result: Result<A>) {
    cb(result.fold(::Right, ::Left))
  }
}