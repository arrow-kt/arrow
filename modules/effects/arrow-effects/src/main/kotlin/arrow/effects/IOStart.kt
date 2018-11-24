package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import arrow.effects.internal.IOConnection
import arrow.effects.internal.Promise
import arrow.effects.internal.asyncIOContinuation
import arrow.effects.internal.toDisposable
import arrow.effects.typeclasses.Fiber
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

fun <A> IOOf<A>.startF(ctx: CoroutineContext): Kind<ForIO, Fiber<ForIO, A>> = IO.async { ioConnection: IOConnection, cb ->
  val conn = IOConnection()
  conn.push(ioConnection.cancel())
  val promise = Promise<A>()

  val a: suspend () -> A = {
    suspendCoroutine { ca: Continuation<A> ->
      IORunLoop.startCancelable(this.fix(), conn) { either: Either<Throwable, A> ->
        either.fold({ error ->
          ca.resumeWith(Result.failure(error))
        }, { a ->
          ca.resumeWith(Result.success(a))
        })
      }
    }
  }

  a.startCoroutine(asyncIOContinuation(ctx) { either ->
    either.fold(
      { cb(it.left()) },
      promise::complete)
  })

  cb(Fiber(IO(promise::get), ioConnection.cancel()).right())
}