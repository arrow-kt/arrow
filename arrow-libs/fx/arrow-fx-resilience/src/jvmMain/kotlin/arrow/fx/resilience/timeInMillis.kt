package arrow.fx.resilience

@Deprecated("This is being removed in Arrow Fx Resilience and kotlin.time.TimeSource is the recommended replacement.")
public actual fun timeInMillis(): Long =
  System.currentTimeMillis()
