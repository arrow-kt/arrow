// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither47

import arrow.core.Either.*
import arrow.core.Either
import arrow.core.filterOrElse

fun main() {
  Right(12).filterOrElse({ it > 10 }, { -1 }) // Result: Right(12)
  Right(7).filterOrElse({ it > 10 }, { -1 })  // Result: Left(-1)

  val left: Either<Int, Int> = Left(12)
  left.filterOrElse({ it > 10 }, { -1 })      // Result: Left(12)
}
