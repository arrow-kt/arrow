package arrow.core.raise

import js.reflect.Reflect

@OptIn(DelicateRaiseApi::class)
internal actual fun NoTrace(raised: Any?, raise: Raise<Any?>): RaiseCancellationException {
  val surrogateError = RaiseCancellationExceptionLike(raised, raise)

  val cancellationExceptionPrototype = RaiseCancellationException::class.js.asDynamic().prototype
  require(Reflect.setPrototypeOf(surrogateError, cancellationExceptionPrototype)) {
    "Unknown error occurred while setting the prototype of constructed JS RaiseCancellationException. Please report this to the Arrow maintainers!"
  }

  // This assignment should be done after the prototype setting
  // because Error prototype contains empty string in message property.
  surrogateError.asDynamic().message = RaiseCancellationExceptionCaptured

  return surrogateError.unsafeCast<RaiseCancellationException>()
}
