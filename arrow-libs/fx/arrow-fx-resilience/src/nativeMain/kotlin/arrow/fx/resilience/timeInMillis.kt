package arrow.fx.resilience

import kotlin.system.getTimeMillis

public actual fun timeInMillis(): Long =
  getTimeMillis()
