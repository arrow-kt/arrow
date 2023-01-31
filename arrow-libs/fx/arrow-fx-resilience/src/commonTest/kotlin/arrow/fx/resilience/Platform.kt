package arrow.fx.resilience

import io.kotest.common.Platform
import io.kotest.common.platform

fun stackSafeIteration(): Int = when (platform) {
  Platform.JVM -> 500_000
  else -> 1000
}
