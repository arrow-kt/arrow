package arrow.core.raise

import arrow.core.ControlCancellationException
import arrow.core.InternalArrowApi
import kotlinx.js.JsPlainObject

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
}

@OptIn(InternalArrowApi::class)
@DelicateRaiseApi
public actual sealed class RaiseCancellationException actual constructor(
  raised: Any?,
  raise: Raise<Any?>
) : ControlCancellationException(RaiseCancellationExceptionCaptured) {
  private val _raised = raised
  private val _raise = raise

  internal actual val raised: Any?
    get() = unsafeCast<RaiseCancellationExceptionLike?>()?.raised ?: _raised

  internal actual val raise: Raise<Any?>
    get() = unsafeCast<RaiseCancellationExceptionLike?>()?.raise ?: _raise
}
