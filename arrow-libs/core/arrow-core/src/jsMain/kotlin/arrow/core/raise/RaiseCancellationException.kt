package arrow.core.raise

import kotlin.coroutines.cancellation.CancellationException

/**
 * TODO: explain
 */
internal external interface RaiseCancellationExceptionLike {
  val raised: Any?
  val raise: Raise<Any?>
}

@DelicateRaiseApi
public actual sealed class RaiseCancellationException actual constructor(
  raised: Any?,
  raise: Raise<Any?>
) : CancellationException(RaiseCancellationExceptionCaptured) {
  private val _raised = raised
  private val _raise = raise

  internal actual val raised: Any?
    get() = unsafeCast<RaiseCancellationExceptionLike?>()?.raised ?: _raised

  internal actual val raise: Raise<Any?>
    get() = unsafeCast<RaiseCancellationExceptionLike?>()?.raise ?: _raise
}
