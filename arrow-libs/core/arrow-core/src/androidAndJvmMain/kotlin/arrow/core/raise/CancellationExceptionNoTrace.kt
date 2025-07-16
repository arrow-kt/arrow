package arrow.core.raise

/*
 * Inspired by KotlinX Coroutines:
 * https://github.com/Kotlin/kotlinx.coroutines/blob/3788889ddfd2bcfedbff1bbca10ee56039e024a2/kotlinx-coroutines-core/jvm/src/Exceptions.kt#L29
 */
@OptIn(DelicateRaiseApi::class)
private class NoTraceImpl(raised: Any?, raise: Raise<Any?>) : RaiseCancellationException(raised, raise) {
  override fun fillInStackTrace(): Throwable {
    // Prevent Android <= 6.0 bug.
    stackTrace = emptyArray()
    // We don't need stacktrace on shift, it hurts performance.
    return this
  }
}

@OptIn(DelicateRaiseApi::class)
internal actual fun NoTrace(raised: Any?, raise: Raise<Any?>): RaiseCancellationException =
  NoTraceImpl(raised, raise)
