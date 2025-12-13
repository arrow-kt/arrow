package arrow.core

import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NonFatalTest {
    val nonFatals: List<Throwable> =
      listOf(
        RuntimeException(),
        Exception(),
        Throwable(),
        NotImplementedError()
      )

  @Test
  fun usingInvoke() {
      nonFatals.forEach {
        NonFatal(it) shouldBe true
      }
    }

  @Test
  fun usingNotFatalOrThrow() {
      nonFatals.forEach {
        it.nonFatalOrThrow() shouldBe it
      }
    }
}
