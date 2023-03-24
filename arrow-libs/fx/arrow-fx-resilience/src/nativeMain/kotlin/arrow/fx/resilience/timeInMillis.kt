package arrow.fx.resilience

import kotlin.system.getTimeMillis

@Deprecated("This is being removed in Arrow Fx Resilience and kotlin.time.TimeSource is the recommended replacement.")
public actual fun timeInMillis(): Long =
  getTimeMillis()
