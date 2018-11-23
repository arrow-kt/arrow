package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.effects.instances.io.applicativeError.attempt
import arrow.effects.instances.io.async.async
import arrow.effects.instances.io.monad.flatMap
import arrow.effects.typeclasses.Fiber
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

fun <A> IOOf<A>.startF(ctx: CoroutineContext): Kind<ForIO, Fiber<ForIO, A>> = Promise.uncancelable<ForIO, A>(IO.async()).flatMap { promise ->
  IO.async<Fiber<ForIO, A>> { ioConnection, cb ->
    val a: suspend () -> A = {
      suspendCoroutine { ca: Continuation<A> ->
        this.attempt().unsafeRunSync().fold({ error ->
          ca.resumeWith(Result.failure(error))
        }, { a ->
          ca.resumeWith(Result.success(a))
        })
      }
    }

    a.startCoroutine(asyncIOContinuation(ctx) {
      it.fold(promise::error, { promise.complete(it) })
        .fix().unsafeRunSync()
    })

    cb(Fiber(promise.get, ioConnection.cancel()).right())
  }
}

private fun <A> asyncIOContinuation(ctx: CoroutineContext, cc: (Either<Throwable, A>) -> Unit): arrow.core.Continuation<A> =
  object : arrow.core.Continuation<A> {
    override val context: CoroutineContext = ctx

    override fun resume(value: A) {
      cc(value.right())
    }

    override fun resumeWithException(exception: Throwable) {
      cc(exception.left())
    }

  }
