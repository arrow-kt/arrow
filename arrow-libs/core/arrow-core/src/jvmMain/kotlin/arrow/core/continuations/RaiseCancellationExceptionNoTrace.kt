package arrow.core.continuations

internal actual class RaiseCancellationExceptionNoTrace actual constructor(
  _raised: Any?,
  raise: Raise<Any?>,
) : RaiseCancellationException(_raised, raise) {
  override fun fillInStackTrace(): Throwable {
    // Prevent Android <= 6.0 bug. https://github.com/Kotlin/kotlinx.coroutines/issues/1866
    stackTrace = emptyArray()
    return this
  }
}
