package arrow.fx.coroutines.kotlinx

import arrow.fx.coroutines.CancelToken
import arrow.fx.coroutines.CancellableContinuation
import arrow.fx.coroutines.Fiber
import arrow.fx.coroutines.never
import arrow.fx.coroutines.startCoroutineCancellable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Launches the source `suspend () -> A` composing cancellation with the `Structured Concurrency` of KotlinX.
 *
 * This will make sure that the source [f] is cancelled whenever it's [CoroutineScope] is cancelled.
 */
suspend fun <A> suspendCancellable(f: suspend () -> A): A =
  suspendCancellableCoroutine { cont ->
    if (cont.isActive) {
      val disposable = f.startCoroutineCancellable(CancellableContinuation(cont.context, cont::resumeWith))
      cont.invokeOnCancellation { disposable() }
    }
  }

/**
 * Unsafely run [fa] and receive the values in a callback [cb] while participating in structured concurrency.
 * Equivalent of [startCoroutineCancellable] but with its cancellation token wired to [CoroutineScope].
 *
 * @see [startCoroutineCancellable] for a version that returns the cancellation token instead.
 */
fun <A> CoroutineScope.unsafeRunScoped(fa: suspend () -> A, cb: (Result<A>) -> Unit): Unit =
  fa.unsafeRunScoped(this, cb)

/**
 * Unsafely run `this` and receive the values in a callback [cb] while participating in structured concurrency.
 * Equivalent of [startCoroutineCancellable] but with its cancellation token wired to [CoroutineScope].
 *
 * @see [startCoroutineCancellable] for a version that returns the cancellation token instead.
 */
fun <A> (suspend () -> A).unsafeRunScoped(
  scope: CoroutineScope,
  cb: (Result<A>) -> Unit
): Unit {
  val newContext = scope.newCoroutineContext(EmptyCoroutineContext)
  val job = newContext[Job]

  if (job == null || job.isActive) {
    val disposable = startCoroutineCancellable(CancellableContinuation(newContext, cb))

    job?.invokeOnCompletion { e ->
      if (e is CancellationException) disposable.invoke()
      else Unit
    }
  }
}

/**
 * Launches [f] as a coroutine in a [Fiber] while participating in structured concurrency.
 * This guarantees resource safety upon cancellation according to [CoroutineScope]'s lifecycle.
 *
 * The returned [Fiber] is automatically cancelled when [CoroutineScope] gets cancelled, or
 * whenever it's [Fiber.cancel] token is invoked. Whichever comes first.
 */
suspend fun <A> ForkScoped(scope: CoroutineScope, f: suspend () -> A): Fiber<A> {
  val newContext = scope.newCoroutineContext(EmptyCoroutineContext)
  val job = newContext[Job]

  val promise = CompletableDeferred<Result<A>>(job)

  return if (job == null || job.isActive) {
    val disposable = f.startCoroutineCancellable(CancellableContinuation(newContext) {
      promise.complete(it)
    })

    job?.invokeOnCompletion { e ->
      if (e is CancellationException) disposable.invoke()
      else Unit
    }

    Fiber({ promise.await().fold({ it }) { e -> throw e } }, CancelToken { disposable.invoke() })
  } else Fiber({ never<A>() }, CancelToken.unit)
}
