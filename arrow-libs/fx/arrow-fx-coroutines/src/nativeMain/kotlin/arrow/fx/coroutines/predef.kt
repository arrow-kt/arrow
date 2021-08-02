package arrow.fx.coroutines

import kotlinx.datetime.Clock

public actual fun timeInMillis(): Long =
  Clock.System.now().toEpochMilliseconds()
