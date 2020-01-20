package arrow.integrations.kotlinx

import arrow.core.Either
import arrow.fx.IO
import arrow.fx.IOOf
import arrow.fx.OnCancel
import arrow.fx.fix
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <A> IO<A>.suspendCancellable() = suspendCancellableCoroutine<A> { cont ->
  val disposable = this.unsafeRunAsyncCancellable(OnCancel.ThrowCancellationException) { result ->
    result.fold(cont::resumeWithException) { cont.resume(it) }//?.let(cont::completeResume)
  }

  cont.invokeOnCancellation { disposable() }
}

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
