package arrow.fx.coroutines

import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

/**
 * Checks for cancellation,
 * when cancellation occurs this coroutine will suspend indefinitely and never continue.
 */
suspend fun cancelBoundary(): Unit =
  suspendCoroutineUninterceptedOrReturn { cont ->
    if (cont.context.connection().isCancelled()) COROUTINE_SUSPENDED
    else Unit
  }
