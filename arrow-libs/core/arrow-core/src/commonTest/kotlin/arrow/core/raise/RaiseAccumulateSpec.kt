package arrow.core.raise

import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.nonEmptyListOf
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class RaiseAccumulateSpec : StringSpec({
  "RaiseAccumulate takes precedence over extension function" {
    either<NonEmptyList<String>, Int> {
      zipOrAccumulate(
        { ensure(false) { "false" } },
        { mapOrAccumulate(1..2) { ensure(false) { "$it: IsFalse" } } }
      ) { _, _ -> 1 }
    } shouldBe nonEmptyListOf("false", "1: IsFalse", "2: IsFalse").left()
  }
})
