package arrow.core.raise

@OptIn(DelicateRaiseApi::class)
private class NoTraceImpl(raised: Any?, raise: Raise<Any?>, isAccumulateError: Boolean) : RaiseCancellationException(raised, raise, isAccumulateError)

@OptIn(DelicateRaiseApi::class)
internal actual fun NoTrace(raised: Any?, raise: Raise<Any?>, isAccumulateError: Boolean): RaiseCancellationException =
  NoTraceImpl(raised, raise, isAccumulateError)
