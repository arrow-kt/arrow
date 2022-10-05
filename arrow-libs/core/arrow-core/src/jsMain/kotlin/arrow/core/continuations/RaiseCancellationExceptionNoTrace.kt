package arrow.core.continuations


internal actual class RaiseCancellationExceptionNoTrace actual constructor(
  _raised: Any?,
  raise: Raise<Any?>,
) : RaiseCancellationException(_raised, raise)
