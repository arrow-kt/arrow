package arrow.core.raise.context

import arrow.core.Either
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.test.fail

class RaiseAccumulateContextTest {

  @Test
  fun `should accumulate errors for 4 actions`() {
    either {
      zipOrAccumulate(
        { raise("Error from action 1") },
        { raise("Error from action 2") },
        { raise("Error from action 3") },
        { raise("Error from action 4") }
      ) { _, _, _, _ ->
        fail("Should never reach here")
      }
    } shouldBe Either.Left(
      NonEmptyList.of(
        "Error from action 1",
        "Error from action 2",
        "Error from action 3",
        "Error from action 4"
      )
    )
  }

  @Test
  fun `should return results when there are no errors for 4 actions`() {
    either<Nel<String>, _> {
      zipOrAccumulate(
        { "a" },
        { "b" },
        { "c" },
        { "d" }
      ) { a, b, c, d ->
        "$a$b$c$d"
      }
    } shouldBe Either.Right("abcd")
  }
}
