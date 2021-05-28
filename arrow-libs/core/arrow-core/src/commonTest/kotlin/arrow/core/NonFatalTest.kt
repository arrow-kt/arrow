package arrow.core

import arrow.core.test.UnitSpec
import io.kotest.matchers.shouldBe

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
  }
}
