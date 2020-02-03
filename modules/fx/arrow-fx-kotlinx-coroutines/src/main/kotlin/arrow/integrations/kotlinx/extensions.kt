package arrow.integrations.kotlinx

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.fx.IOOf
import arrow.fx.IOResult
import arrow.fx.fix
import arrow.fx.unsafeRunAsyncCancellable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <A> IOOf<Nothing, A>.suspendCancellable(): A = suspendCancellableCoroutine { cont ->
  val disposable = fix().unsafeRunAsyncCancellable { either ->
    either.fold(cont::resumeWithException) { cont.resume(it) }
  }

  cont.invokeOnCancellation { disposable() }
}

@JvmName("suspendEitherCancellable")
suspend fun <E, A> IOOf<E, A>.suspendCancellable(): Either<E, A> = suspendCancellableCoroutine { cont ->
  val disposable = fix().unsafeRunAsyncCancellableEither { result ->
    result.fold(cont::resumeWithException, { cont.resume((Left(it))) }) { cont.resume(Right(it)) }
  }

  cont.invokeOnCancellation { disposable() }
}

fun <E, A> CoroutineScope.unsafeRunIO(io: IOOf<E, A>, f: (IOResult<E, A>) -> Unit): Unit =
  io.unsafeRunScoped(this, f)

fun <E, A> IOOf<E, A>.unsafeRunScoped(
  scope: CoroutineScope,
  f: (IOResult<E, A>) -> Unit
): Unit {
  val newContext = scope.newCoroutineContext(EmptyCoroutineContext)
  val job = newContext[Job]

  val disposable = fix().unsafeRunAsyncCancellableEither(cb = f)

  job?.invokeOnCompletion { e ->
    if (e is CancellationException) disposable()
    else Unit
  }
}
