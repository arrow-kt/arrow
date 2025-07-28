package arrow.core.raise

@OptIn(DelicateRaiseApi::class)
private class NoTraceImpl(raised: Any?, raise: Raise<Any?>) : RaiseCancellationException(raised, raise)

@OptIn(DelicateRaiseApi::class)
internal actual fun NoTrace(raised: Any?, raise: Raise<Any?>): RaiseCancellationException =
  NoTraceImpl(raised, raise)
