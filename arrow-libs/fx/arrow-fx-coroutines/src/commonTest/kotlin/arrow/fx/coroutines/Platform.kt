package arrow.fx.coroutines

import io.kotest.common.Platform
import io.kotest.common.platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext

fun stackSafeIteration(): Int = when (platform) {
  Platform.JVM -> 20_000
  else -> 1000
}

// The normal dispatcher with 'runTest' does some magic
// which doesn't go well with 'parZip', 'parMap', and 'raceN'
fun runTestUsingDefaultDispatcher(testBody: suspend TestScope.() -> Unit): TestResult = runTest {
  withContext(Dispatchers.Default) {
    testBody()
  }
}
