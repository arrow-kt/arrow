package arrow.effects

import arrow.core.*
import arrow.effects.internal.*
import arrow.effects.typeclasses.Fiber
import kotlin.coroutines.*
import kotlin.coroutines.Continuation

/**
 * Create a new [IO] that executes the receiver [IO].
 *
 * @receiver [IO] to execute on [ctx] within a new suspended [IO].
 * @param ctx [CoroutineContext] to execute the source [IO] on.
 * @return [IO] with suspended execution of source [IO] on context [ctx].
 */
fun <A> IOOf<A>.startF(ctx: CoroutineContext): IO<Fiber<ForIO, A>> {

  val promise = Promise.unsafe<Either<Throwable, A>>()

  // A new IOConnection, because its cancellation is now decoupled from our current one.
  // We use this [IOConnection] to start [IORunLoop] and cancel the [Fiber].
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

  a.startCoroutine(asyncContinuation(ctx) { either ->
    either.fold(
      { promise.complete(it.left()) },
      { promise.complete(it.right()) }
    )
  })

  return IO.just(Fiber(promise, conn))
}
