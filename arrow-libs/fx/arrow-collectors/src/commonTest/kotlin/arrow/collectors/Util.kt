package arrow.collectors

import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext

fun runTestWithDelay(testBody: suspend () -> Unit): TestResult = runTest {
  withContext(Dispatchers.Default) {
    testBody()
  }
}

fun runTestOverLists(block: suspend PropertyContext.(List<Int>) -> Unit): TestResult = runTestWithDelay {
  checkAll(Arb.list(Arb.int(-1000 .. 1000), range = 0 .. 20), block)
}
