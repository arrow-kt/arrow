package arrow.core.raise

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@Suppress("UNREACHABLE_CODE")
class ResultSpec : StringSpec({
  val boom = RuntimeException("Boom!")

  "Result - exception" {
    result {
      throw boom
    } shouldBe Result.failure(boom)
  }

  "Result - success" {
    result { 1 } shouldBe Result.success(1)
  }

  "Result - raise" {
    result { raise(boom) } shouldBe Result.failure(boom)
  }
})
