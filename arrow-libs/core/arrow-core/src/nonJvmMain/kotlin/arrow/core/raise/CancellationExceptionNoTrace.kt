package arrow.core.raise

@OptIn(DelicateRaiseApi::class)
@Suppress(
  "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING",
  "SEALED_INHERITOR_IN_DIFFERENT_MODULE"
)
internal actual class NoTrace actual constructor(raised: Any?, raise: Raise<Any?>) : RaiseCancellationException(raised, raise)
