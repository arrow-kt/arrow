package arrow.integrations.kotlinx

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.fx.IO
import arrow.fx.IOOf
import arrow.fx.IOPartialOf
import arrow.fx.IOResult
import arrow.fx.fix
import arrow.fx.flatMap
import arrow.fx.unsafeRunAsyncCancellable
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
suspend fun <A> IOOf<Nothing, A>.suspendCancellable(): A =
  suspendCancellableCoroutine { cont ->
    if (cont.isActive) {
      val disposable = fix().unsafeRunAsyncCancellable { either ->
        either.fold(cont::resumeWithException) { cont.resume(it) }
      }

      cont.invokeOnCancellation { disposable() }
    }
  }

@JvmName("suspendEitherCancellable")
suspend fun <E, A> IOOf<E, A>.suspendCancellable(): Either<E, A> = suspendCancellableCoroutine { cont ->
  if (cont.isActive) {
    val disposable = fix().unsafeRunAsyncCancellableEither { result ->
      result.fold(cont::resumeWithException, { cont.resume((Left(it))) }) { cont.resume(Right(it)) }
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
fun <E, A> CoroutineScope.unsafeRunIO(io: IOOf<E, A>, cb: (IOResult<E, A>) -> Unit): Unit =
  io.unsafeRunScoped(this, cb)

/**
 * Unsafely run an [IO] and receive the values in a callback [cb] while participating in structured concurrency.
 * Equivalent of [IO.unsafeRunAsyncCancellable] but with its cancellation token wired to [CoroutineScope].
 *
 * @see [IO.unsafeRunAsyncCancellable] for a version that returns the cancellation token instead.
 */
fun <E, A> IOOf<E, A>.unsafeRunScoped(
  scope: CoroutineScope,
  cb: (IOResult<E, A>) -> Unit
) {
  val newContext = scope.newCoroutineContext(EmptyCoroutineContext)
  val job = newContext[Job]

  if (job == null || job.isActive) {
    val disposable = fix().unsafeRunAsyncCancellableEither(cb = cb)

    job?.invokeOnCompletion { e ->
      if (e is CancellationException) disposable?.invoke()
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
fun <E, A> IOOf<E, A>.forkScoped(scope: CoroutineScope): IO<E, Fiber<IOPartialOf<E>, A>> =
  IO.async { cb ->
    val newContext = scope.newCoroutineContext(EmptyCoroutineContext)
    val job = newContext[Job]

    val promise = UnsafePromise<E, A>()

    if (job == null || job.isActive) {
      val disposable = IO.unit.continueOn(newContext).flatMap { fix() }
        .unsafeRunAsyncCancellableEither(cb = promise::complete)

      job?.invokeOnCompletion { e ->
        if (e is CancellationException) disposable.invoke()
        else Unit
      }

      cb(IOResult.Success(IOFiber(promise, disposable)))
    } else cb(IOResult.Success(Fiber<IOPartialOf<E>, A>(IO.never, IO.unit)))
  }

private fun <E, A> IOFiber(promise: UnsafePromise<E, A>, conn: Disposable): Fiber<IOPartialOf<E>, A> {
  val join: IO<E, A> = IO.cancellable { cb ->
    promise.get(cb)

    IO { promise.remove(cb) }
  }

  return Fiber(join, IO { conn.invoke() })
}
