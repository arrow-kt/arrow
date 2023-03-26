package arrow.core.test

import io.kotest.common.Platform
import io.kotest.common.platform

fun stackSafeIteration(): Int = when (platform) {
  Platform.JVM -> 200_000
  else -> 1000
}
