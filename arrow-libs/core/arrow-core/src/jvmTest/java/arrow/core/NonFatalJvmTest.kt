package arrow.core

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class NonFatalJvmTest {
  val fatals: List<Throwable> =
    listOf(
      InterruptedException(),
      StackOverflowError(),
      OutOfMemoryError(),
      LinkageError(),
      object : VirtualMachineError() {
      },
    )

  @Test fun testFatalsUsingInvoke() = runTest {
    fatals.forEach {
      NonFatal(it) shouldBe false
    }
  }

  @Test fun testFatalsUsingThrowableNonFatalOrThrow() = runTest {
    fatals.forEach {
      shouldThrowAny {
        it.nonFatalOrThrow()
      }
    }
  }
}
