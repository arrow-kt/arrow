package arrow.core.raise

/*
 * Inspired by KotlinX Coroutines:
 * https://github.com/Kotlin/kotlinx.coroutines/blob/3788889ddfd2bcfedbff1bbca10ee56039e024a2/kotlinx-coroutines-core/jvm/src/Exceptions.kt#L29
 */
@DelicateRaiseApi
@Suppress(
  "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
  "SEALED_INHERITOR_IN_DIFFERENT_MODULE"
)
@PublishedApi
internal actual class NoTrace actual constructor(raised: Any?, raise: Raise<Any?>) : RaiseCancellationException(raised, raise) {
  override fun fillInStackTrace(): Throwable {
    // Prevent Android <= 6.0 bug.
    stackTrace = emptyArray()
    // We don't need stacktrace on shift, it hurts performance.
    return this
  }
}

@PublishedApi
internal actual fun Throwable.copyStacktrace(from: Throwable) {
  this.stackTrace = from.stackTrace
}
