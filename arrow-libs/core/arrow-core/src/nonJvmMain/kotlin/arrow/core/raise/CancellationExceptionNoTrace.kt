package arrow.core.raise

@Suppress(
  "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
  "SEALED_INHERITOR_IN_DIFFERENT_MODULE"
)
internal actual class NoTrace actual constructor(raised: Any?, raise: Raise<*>) : RaiseCancellationException(raised, raise)
