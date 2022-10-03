// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither57

import arrow.core.Either
import arrow.core.catch
import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.matchers.shouldBe

fun main() {
  val boom: Either<Throwable, Int> = Either.catch { throw RuntimeException("Boom!") }

  val caught: Either<Nothing, Int> = boom.catch { _: Throwable -> 1 }
  val failure: Either<String, Int> = boom.catch { _: Throwable -> shift("failure") }

  shouldThrowUnit<RuntimeException> {
    val caught2: Either<Nothing, Int> = boom.catch { _: IllegalStateException -> 1 }
  }

  caught shouldBe Either.Right("resolved")
  failure shouldBe Either.Left("failure")
}
