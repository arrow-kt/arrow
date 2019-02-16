package arrow.core

import arrow.test.UnitSpec
import io.kotlintest.runner.junit4.KotlinTestRunner
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrowAny
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
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
    "Test nonfatals using Throwable#nonFatal" {
      nonFatals.forEach {
        it.nonFatal() shouldBe it
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
    "Test fatals using Throwable#nonFatal" {
      fatals.forEach {
        shouldThrowAny {
          it.nonFatal()
        }
      }
    }
  }
}
