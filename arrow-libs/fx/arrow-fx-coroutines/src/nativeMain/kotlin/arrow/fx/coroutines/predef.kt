package arrow.fx.coroutines

import kotlin.system.getTimeMillis

public actual fun timeInMillis(): Long =
  getTimeMillis()
