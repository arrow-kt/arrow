package arrow.core.raise

private external interface HasPrototype {
  val prototype: Any?
}

private external interface HasMutableMessage {
  var message: String?
}

// TODO: import from Kotlin Wrappers
private external object Reflect {
  fun setPrototypeOf(target: Any, proto: Any?): Boolean
}

@OptIn(DelicateRaiseApi::class)
internal actual fun NoTrace(raised: Any?, raise: Raise<Any?>): RaiseCancellationException {
  val surrogateError = js("{raised: raised, raise: raise}")

  val cancellationExceptionPrototype = RaiseCancellationException::class.js.unsafeCast<HasPrototype>().prototype
  Reflect.setPrototypeOf(surrogateError, cancellationExceptionPrototype)
  surrogateError.unsafeCast<HasMutableMessage>().message = RaiseCancellationExceptionCaptured

  return surrogateError.unsafeCast<RaiseCancellationException>()
}
