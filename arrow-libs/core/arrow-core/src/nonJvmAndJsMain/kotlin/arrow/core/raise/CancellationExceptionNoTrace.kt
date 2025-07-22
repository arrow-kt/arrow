package arrow.core.raise

@OptIn(DelicateRaiseApi::class)
@Suppress(
  "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
  "SEALED_INHERITOR_IN_DIFFERENT_MODULE"
)
private class NoTraceImpl(raised: Any?, raise: Raise<Any?>) : RaiseCancellationException(raised, raise)

@DelicateRaiseApi
internal actual fun NoTrace(raised: Any?, raise: Raise<Any?>): RaiseCancellationException = NoTraceImpl(raised, raise)
