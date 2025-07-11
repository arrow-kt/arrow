package arrow.core.raise

import js.reflect.Reflect

private external interface HasMutableMessage {
  var message: String?
}

@OptIn(DelicateRaiseApi::class)
internal actual fun NoTrace(raised: Any?, raise: Raise<Any?>): RaiseCancellationException {
  val surrogateError = RaiseCancellationExceptionLike(raised, raise)

  val cancellationExceptionPrototype = RaiseCancellationException::class.js.asDynamic().prototype
  Reflect.setPrototypeOf(surrogateError, cancellationExceptionPrototype)
  surrogateError.unsafeCast<HasMutableMessage>().message = RaiseCancellationExceptionCaptured

  return surrogateError.unsafeCast<RaiseCancellationException>()
}
