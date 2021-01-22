package arrow.integrations.kotlinx

import arrow.core.Either
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.IODeprecation
import arrow.fx.IOOf
import arrow.fx.fix
import arrow.fx.internal.UnsafePromise
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.Fiber
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Safely execute the source [IO] in a suspending context,
 * doing so with [suspendCancellable] makes it participate in structured concurrency.
 *
 * This will make sure that the source [IO] is cancelled whenever it's [CoroutineScope] is cancelled.
 */
@Deprecated(IODeprecation)
suspend fun <A> IOOf<A>.suspendCancellable(): A =
  suspendCancellableCoroutine { cont ->
    if (cont.isActive) {
      val disposable = fix().unsafeRunAsyncCancellable { result ->
        result.fold<Unit>(cont::resumeWithException, cont::resume)
      }

      cont.invokeOnCancellation { disposable() }
    }
  }

/**
 * Unsafely run an [IO] and receive the values in a callback [cb] while participating in structured concurrency.
 * Equivalent of [IO.unsafeRunAsyncCancellable] but with its cancellation token wired to [CoroutineScope].
 *
 * @see [IO.unsafeRunAsyncCancellable] for a version that returns the cancellation token instead.
 */
@Deprecated(IODeprecation)
fun <A> CoroutineScope.unsafeRunIO(io: IOOf<A>, cb: (Either<Throwable, A>) -> Unit): Unit =
  io.unsafeRunScoped(this, cb)

/**
 * Unsafely run an [IO] and receive the values in a callback [cb] while participating in structured concurrency.
 * Equivalent of [IO.unsafeRunAsyncCancellable] but with its cancellation token wired to [CoroutineScope].
 *
 * @see [IO.unsafeRunAsyncCancellable] for a version that returns the cancellation token instead.
 */
@Deprecated(IODeprecation)
fun <A> IOOf<A>.unsafeRunScoped(
  scope: CoroutineScope,
  cb: (Either<Throwable, A>) -> Unit
): Unit {
  val newContext = scope.newCoroutineContext(EmptyCoroutineContext)
  val job = newContext[Job]

  if (job == null || job.isActive) {
    val disposable = fix().unsafeRunAsyncCancellable(cb = cb)

    job?.invokeOnCompletion { e ->
      if (e is CancellationException) disposable()
      else Unit
    }
  }
}

/**
 * Fires a [Fiber] while wiring it to a [CoroutineScope].
 * This guarantees resource safety upon cancellation according to [CoroutineScope]'s lifecycle.
 *
 * This returns a [Fiber] that automatically gets cancelled when [CoroutineScope] gets cancelled.
 */
@Deprecated(IODeprecation)
fun <A> IOOf<A>.forkScoped(scope: CoroutineScope): IO<Fiber<ForIO, A>> =
  IO.async { cb ->
    val newContext = scope.newCoroutineContext(EmptyCoroutineContext)
    val job = newContext[Job]

    val promise = UnsafePromise<A>()

    if (job == null || job.isActive) {
      val disposable = IO.unit.continueOn(newContext).flatMap { fix() }
        .unsafeRunAsyncCancellable(cb = promise::complete)

      job?.invokeOnCompletion { e ->
        if (e is CancellationException) disposable()
        else Unit
      }

      cb(Either.Right(IOFiber(promise, disposable)))
    } else cb(Either.Right(Fiber(IO.never, IO.unit)))
  }

private fun <A> IOFiber(promise: UnsafePromise<A>, conn: Disposable): Fiber<ForIO, A> {
  val join: IO<A> = IO.cancellable { cb ->
    promise.get(cb)

    IO { promise.remove(cb) }
  }

  return Fiber(join, IO { conn.invoke() })
}
