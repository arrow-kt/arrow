package arrow.fx.coroutines

import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.common.runBlocking
import kotlinx.coroutines.test.runTest

fun stackSafeIteration(): Int = when (platform) {
  Platform.JVM -> 500_000
  else -> 1000
}

fun runBlockingOnNative(testBody: suspend () -> Unit) {
  if (platform == Platform.Native)
    runBlocking(testBody)
  else
    runTest { testBody() }
}
