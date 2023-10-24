package arrow.atomic

import io.kotest.common.Platform
import io.kotest.common.platform
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.withContext

fun stackSafeIteration(): Int = when (platform) {
  Platform.JVM -> 500_000
  else -> 1000
}

fun runTestWithDelay(testBody: suspend () -> Unit): TestResult = runTest {
  withContext(Dispatchers.Default) {
    testBody()
  }
}
