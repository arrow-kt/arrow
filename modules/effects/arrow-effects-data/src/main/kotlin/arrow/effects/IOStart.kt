package arrow.effects

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.effects.internal.IOFiber
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.Fiber
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

/**
 * Create a new [IO] that upon execution starts the receiver [IO] within a [Fiber] on [ctx].
 *
 * ```kotlin:ank:playground
 * import arrow.effects.*
 * import arrow.effects.extensions.io.async.async
 * import arrow.effects.extensions.io.monad.binding
 * import kotlinx.coroutines.Dispatchers
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   binding {
 *     val promise = Promise.uncancelable<ForIO, Int>(IO.async()).bind()
 *     val fiber = promise.get().startFiber(Dispatchers.Default).bind()
 *     promise.complete(1).bind()
 *     fiber.join().bind()
 *   }.unsafeRunSync() == 1
 *   //sampleEnd
 * }
 * ```
 *
 * @receiver [IO] to execute on [ctx] within a new suspended [IO].
 * @param ctx [CoroutineContext] to execute the source [IO] on.
 * @return [IO] with suspended execution of source [IO] on context [ctx].
 */
fun <A> IOOf<A>.startFiber(ctx: CoroutineContext): IO<Fiber<ForIO, A>> = IO {
  val promise = UnsafePromise<A>()

  // A new IOConnection, because its cancellation is now decoupled from our current one.
  // We use this [IOConnection] to start [IORunLoop] and cancel the [Fiber].
  val conn = IOConnection()

  val a: suspend () -> A = {
    suspendCoroutine { ca: Continuation<A> ->
      IORunLoop.startCancelable(this, conn) { either: Either<Throwable, A> ->
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

  IOFiber(promise, conn)
}
