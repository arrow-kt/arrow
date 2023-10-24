package arrow.atomic

import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.withContext

fun runTestWithDelay(testBody: suspend () -> Unit): TestResult = runTest {
  withContext(Dispatchers.Default) {
    testBody()
  }
}

