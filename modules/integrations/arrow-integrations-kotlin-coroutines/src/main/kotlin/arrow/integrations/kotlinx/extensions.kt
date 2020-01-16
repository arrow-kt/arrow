package arrow.integrations.kotlinx

import arrow.core.identity
import arrow.fx.IO
import arrow.fx.IOOf
import arrow.fx.extensions.io.async.shift
import arrow.fx.extensions.io.bracket.guaranteeCase
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

// Where does the exception go??? ExceptionHandler?
fun CoroutineScope.launchIO(
  ctx: CoroutineContext = EmptyCoroutineContext,
  block: () -> IO<Unit>
) {
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

object KotlinXSyntax {
  suspend fun <A> IO<A>.suspended(): A = suspendCancellableCoroutine<A> { cont ->
    val disposable = cont.context.shift().followedBy(this)
      .unsafeRunAsyncCancellable { result ->
        result.fold({ throw it }, ::identity)
      }

    cont.invokeOnCancellation { disposable.invoke() }
  }
}

fun <A> IOOf<A>.onCancel(token: IOOf<Unit>): IO<A> =
  guaranteeCase { case ->
    when (case) {
      arrow.fx.typeclasses.ExitCase.Canceled -> token
      else -> IO.unit
    }
  }
