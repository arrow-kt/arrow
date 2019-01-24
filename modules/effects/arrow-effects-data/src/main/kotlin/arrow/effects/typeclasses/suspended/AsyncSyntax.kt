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

  override fun <A> f(fa: suspend () -> A): Kind<F, A> =
    super.async { cb ->
      AsyncContinuation(cb).apply {
        fa.startCoroutine(this)
      }
    }

  private suspend fun <A> asyncOp(fb: Async<F>.() -> Kind<F, A>): A =
    run<Async<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> async(unit: Unit = Unit, fa: suspend ((Either<Throwable, A>) -> Unit) -> Unit): A =
    asyncOp { asyncF(fa.flatLiftM()) }

  suspend fun <A> CoroutineContext.defer(f: suspend () -> A): A =
    asyncOp { defer(this@defer) { f.liftM() } }

  suspend fun continueOn(ctx: CoroutineContext): Unit =
    asyncOp { ctx.shift() }

}

internal open class AsyncContinuation<A>(val cb: (Either<Throwable, A>) -> Unit) : kotlin.coroutines.Continuation<A> {
  override val context: CoroutineContext
    get() = EmptyCoroutineContext // TODO this should probably use the default dispatcher by default.
  override fun resumeWith(result: Result<A>) {
    cb(result.fold(::Right, ::Left))
  }
}