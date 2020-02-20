package arrow.core

import arrow.test.UnitSpec
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny

class NonFatalTest : UnitSpec() {
  init {
    val nonFatals: List<Throwable> =
      listOf(
        RuntimeException(),
        Exception(),
        Throwable(),
        NotImplementedError()
      )

    "Test nonfatals using #invoke()" {
      nonFatals.forEach {
        NonFatal(it) shouldBe true
      }
    }
    "Test nonfatals using Throwable#nonFatalOrThrow" {
      nonFatals.forEach {
        it.nonFatalOrThrow() shouldBe it
      }
    }

    val fatals: List<Throwable> =
      listOf(
        InterruptedException(),
        StackOverflowError(),
        OutOfMemoryError(),
        LinkageError(),
        object : VirtualMachineError() {
        }
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
  }
}
