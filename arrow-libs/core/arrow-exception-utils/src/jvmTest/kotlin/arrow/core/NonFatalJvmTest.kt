package arrow.core

import io.kotest.assertions.AssertionErrorBuilder.Companion.fail
import io.kotest.matchers.assertionCounter
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

  @Test
  fun testFatalsUsingInvoke() = runTest {
      fatals.forEach {
          NonFatal(it) shouldBe false
      }
  }

  @Test
  fun testFatalsUsingThrowableNonFatalOrThrow() = runTest {
      fatals.forEach {
          shouldThrowAny {
              it.nonFatalOrThrow()
          }
      }
  }
}

inline fun shouldThrowAny(block: () -> Any?) {
  assertionCounter.inc()
  try {
    val _ = block()
    null
  } catch (e: Throwable) {
    e
  } ?: fail("Expected a throwable, but nothing was thrown.")
}

