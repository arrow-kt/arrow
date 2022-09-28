// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither56

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun main() {
  val boom = Either.catch { throw RuntimeException("Boom!") }

  boom.catch { _: Throwable -> "resolved" } shouldBe Either.Right("resolved")

  boom.catch { _: Throwable -> shift("failure") } shouldBe Either.Left("failure")

  shouldThrow<RuntimeException> {
    boom.catch { _: IllegalStateException -> "recover" }
  }
}
