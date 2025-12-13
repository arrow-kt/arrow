package arrow.core.raise

import kotlinx.js.JsPlainObject
import kotlin.coroutines.cancellation.CancellationException

/**
 * There is no direct way to create an instance of [Error](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Error) without a stack in JS.
 * But we can throw any object or primitive in the JS platform.
 * This interface is used to enforce structural contract
 * between actual instance of [NoTrace] object and [RaiseCancellationException] class.
 */
@JsPlainObject
internal external interface RaiseCancellationExceptionLike {
  val raised: Any?
  val raise: Raise<Any?>
  val isAccumulateError: Boolean
}

@DelicateRaiseApi
public actual sealed class RaiseCancellationException actual constructor(
  raised: Any?,
  raise: Raise<Any?>,
  isAccumulateError: Boolean,
) : CancellationException(RaiseCancellationExceptionCaptured) {
  private val _raised = raised
  private val _raise = raise
  private val _isAccumulateError = isAccumulateError

  internal actual val raised: Any?
    get() = unsafeCast<RaiseCancellationExceptionLike?>()?.raised ?: _raised

  internal actual val raise: Raise<Any?>
    get() = unsafeCast<RaiseCancellationExceptionLike?>()?.raise ?: _raise

  internal actual val isAccumulateError: Boolean
    get() = unsafeCast<RaiseCancellationExceptionLike?>()?.isAccumulateError ?: _isAccumulateError
}
