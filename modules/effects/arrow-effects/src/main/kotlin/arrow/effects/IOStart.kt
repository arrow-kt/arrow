package arrow.effects

import arrow.core.*
import arrow.effects.internal.*
import arrow.effects.typeclasses.Fiber
import kotlin.coroutines.*
import kotlin.coroutines.Continuation

/**
 * Create a new [IO] that upon execution starts the receiver [IO] within a [Fiber] on [ctx].
 *
 * {: data-executable='true'}
 *
 * ```kotlin:ank
 * import arrow.effects.*
 * import arrow.effects.instances.io.async.async
 * import arrow.effects.instances.io.monad.binding
 * import kotlinx.coroutines.Dispatchers
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   binding {
 *     val promise = Promise.uncancelable<ForIO, Int>(IO.async()).bind()
 *     val fiber = promise.get.startF(Dispatchers.Default).bind()
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
fun <A> IOOf<A>.startF(ctx: CoroutineContext): IO<Fiber<ForIO, A>> = IO {
  val promise = Promise.unsafe<Either<Throwable, A>>()

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

  Fiber(promise, conn)
}
