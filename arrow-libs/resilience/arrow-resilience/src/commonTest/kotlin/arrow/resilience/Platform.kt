package arrow.resilience

import arrow.resilience.common.Platform
import arrow.resilience.common.platform

fun stackSafeIteration(): Int = when (platform) {
  Platform.JVM -> 20_000
  else -> 1_000
}
