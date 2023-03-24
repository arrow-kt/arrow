package arrow.fx.resilience

import arrow.fx.resilience.common.Platform
import arrow.fx.resilience.common.platform

fun stackSafeIteration(): Int = when (platform) {
  Platform.JVM -> 20_000
  else -> 1_000
}
