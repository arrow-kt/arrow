package arrow.core.raise

@DelicateRaiseApi
@Suppress(
  "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
  "SEALED_INHERITOR_IN_DIFFERENT_MODULE"
)
@PublishedApi
internal actual class NoTrace actual constructor(raised: Any?, raise: Raise<Any?>) : RaiseCancellationException(raised, raise)

@PublishedApi
internal actual fun Throwable.copyStacktrace(from: Throwable) { }
