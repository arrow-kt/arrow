package arrow.integrations.kotlinx

import arrow.core.Either
import arrow.fx.IOOf
import arrow.fx.fix
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <A> IOOf<A>.suspendCancellable(): A = suspendCancellableCoroutine { cont ->
  val disposable = fix().unsafeRunAsyncCancellable { result ->
    result.fold<Unit>(cont::resumeWithException, cont::resume)
  }

  cont.invokeOnCancellation { disposable() }
}

fun <A> CoroutineScope.unsafeRunIO(io: IOOf<A>, f: (Either<Throwable, A>) -> Unit): Unit =
  io.unsafeRunScoped(this, f)

fun <A> IOOf<A>.unsafeRunScoped(
  scope: CoroutineScope,
  f: (Either<Throwable, A>) -> Unit
): Unit {
  val newContext = scope.newCoroutineContext(EmptyCoroutineContext)
  val job = newContext[Job]

  val disposable = fix().unsafeRunAsyncCancellable(cb = f)

  job?.invokeOnCompletion { e ->
    if (e is CancellationException) disposable()
    else Unit
  }
}
