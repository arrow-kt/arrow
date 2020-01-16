package arrow.integrations.kotlinx

import arrow.core.Either
import arrow.core.identity
import arrow.fx.IO
import arrow.fx.IOOf
import arrow.fx.extensions.io.async.shift
import arrow.fx.fix
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.newCoroutineContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

// Where does the exception go??? ExceptionHandler?
fun CoroutineScope.launchIO(
  ctx: CoroutineContext = EmptyCoroutineContext,
  block: () -> IO<Unit>
): Unit {
  val newContext = newCoroutineContext(ctx)
  val job = newContext[Job]

  val disposable = newContext.shift()
    .followedBy(block())
    .unsafeRunAsyncCancellable { result ->
      result.fold({ throw it }, ::identity)
    }

  job?.invokeOnCompletion { e ->
    if (e is CancellationException) disposable()
    else Unit
  }
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
