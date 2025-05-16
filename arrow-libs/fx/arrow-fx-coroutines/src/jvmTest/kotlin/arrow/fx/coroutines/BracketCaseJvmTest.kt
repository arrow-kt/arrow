package arrow.fx.coroutines

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.DefaultAsserter.fail
import kotlin.test.Test

class BracketCaseJvmTest {
  @Test
  fun blowBracketOnFatal() = runTest {
    val error = shouldThrow<LinkageError> {
      bracket({ }, { throw LinkageError("BOOM!") }) { fail("Should never come here") }
    }
    error.message shouldBe "BOOM!"
    error.suppressedExceptions.shouldBeEmpty()
  }

  @Test
  fun blowBracketOnFatalInRelease() = runTest {
    val error = shouldThrow<LinkageError> {
      bracket({ }, { throw RuntimeException() }) { throw LinkageError("BOOM!") }
    }
    error.message shouldBe "BOOM!"
    error.suppressedExceptions.shouldBeEmpty()
  }
}
