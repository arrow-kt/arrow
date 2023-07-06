package arrow.core

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class NonFatalJvmTest : StringSpec({
  val fatals: List<Throwable> =
    listOf(
      InterruptedException(),
      StackOverflowError(),
      OutOfMemoryError(),
      LinkageError(),
      object : VirtualMachineError() {
      },
    )

  "Test fatals using #invoke()" {
    fatals.forEach {
      NonFatal(it) shouldBe false
    }
  }
  "Test fatals using Throwable#nonFatalOrThrow" {
    fatals.forEach {
      shouldThrowAny {
        it.nonFatalOrThrow()
      }
    }
  }
})
