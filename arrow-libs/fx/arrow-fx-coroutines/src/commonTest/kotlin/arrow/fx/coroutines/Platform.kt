package arrow.fx.coroutines

import io.kotest.common.Platform
import io.kotest.common.platform
import io.kotest.common.runBlocking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
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
