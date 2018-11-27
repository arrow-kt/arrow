package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.effects.IO.Companion.just
import arrow.effects.internal.*
import arrow.effects.typeclasses.Fiber
import kotlin.coroutines.*
import kotlin.coroutines.Continuation

fun <A> IOOf<A>.startF(ctx: CoroutineContext): Kind<ForIO, Fiber<ForIO, A>> = IO.async { ioConnection: IOConnection, cb ->
  val promise = Promise.unsafe<A>()
  val conn = IOConnection()

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
      promise::complete
    )
  })

  val join: IO<A> = IO.defer {
    try {
      just(promise.get.value())
    } catch (t: Throwable) {
      IO.raiseError<A>(t)
    }
  }

  val cancel = ioConnection.cancel()

  cb(Fiber(join, cancel).right())
}
