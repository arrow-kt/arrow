package arrow.core.raise

@OptIn(DelicateRaiseApi::class)
@Suppress(
  "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
  "SEALED_INHERITOR_IN_DIFFERENT_MODULE"
)
private class NoTraceImpl(raised: Any?, raise: Raise<Any?>) : RaiseCancellationException(raised, raise)

private val Error = js("Error")

// See https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Error/stackTraceLimit
private inline fun <R> withNoStackTrace(block: () -> R): R {
  val prevLimit = Error.stackTraceLimit
  Error.stackTraceLimit = 0 // Disable stack trace generation
  try {
    return block()
  } finally {
    Error.stackTraceLimit = prevLimit // Restore original limit
  }
}

@DelicateRaiseApi
internal actual fun NoTrace(raised: Any?, raise: Raise<Any?>): RaiseCancellationException = withNoStackTrace {
  NoTraceImpl(raised, raise)
}
