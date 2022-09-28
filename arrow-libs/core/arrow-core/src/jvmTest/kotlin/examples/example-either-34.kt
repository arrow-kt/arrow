// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither34

import arrow.core.Either
import io.kotest.matchers.shouldBe

fun main() {
  fun Either<Exception, Int>.computeResult(): Int =
    fold({ error: Exception -> -1 }) { res: Int -> res + 1 }

  Either.Right(1).computeResult() shouldBe 2
  Either.Left(RuntimeException("Boom!")).computeResult() shouldBe -1
}
