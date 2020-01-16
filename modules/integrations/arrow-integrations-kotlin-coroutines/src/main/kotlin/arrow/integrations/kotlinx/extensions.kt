package arrow.integrations.kotlinx

import arrow.core.identity
import arrow.fx.IO
import arrow.fx.extensions.io.async.shift
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.newCoroutineContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun CoroutineScope.launchIO(
  ctx: CoroutineContext = EmptyCoroutineContext,
  block: () -> IO<Unit>
): Unit {
  val newContext = newCoroutineContext(ctx)
  val job = newContext[Job]

  val disposable = newContext.shift().followedBy(block())
    .unsafeRunAsyncCancellable { result ->
      result.fold({ throw it }, ::identity)
    }

  job?.invokeOnCompletion { e ->
    if (e is CancellationException) disposable()
    else Unit
  }
}
