package arrow.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class NonFatalTest : StringSpec() {
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
