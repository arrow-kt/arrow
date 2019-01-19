package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.effects.typeclasses.Async
import arrow.typeclasses.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

interface AsyncSyntax<F> : MonadDeferSyntax<F>, Async<F> {

  override fun <A> (suspend () -> A).k(): Kind<F, A> =
    super.async { cb ->
      startCoroutine(object : Continuation<A> {
        override fun resume(value: A) {
          cb(value.right())
        }

        override fun resumeWithException(exception: Throwable) {
          cb(exception.left())
        }

        override val context: CoroutineContext = EmptyCoroutineContext
      })
    }


  private suspend fun <A> asyncOp(fb: Async<F>.() -> Kind<F, A>): A =
    run<Async<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> async(unit: Unit = Unit, fa: suspend ((Either<Throwable, A>) -> Unit) -> Unit): A =
    asyncOp { asyncF(fa.kr()) }

  suspend fun <A> CoroutineContext.defer(f: suspend () -> A): A =
    asyncOp { defer(this@defer) { f.k() } }

  suspend fun CoroutineContext.shift(unit: Unit = Unit): Unit =
    asyncOp { shift() }

}