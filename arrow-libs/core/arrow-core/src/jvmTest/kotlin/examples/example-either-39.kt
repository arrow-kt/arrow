// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither39

import arrow.core.Either
import arrow.core.Either.Left

fun main() {
 Either.Right(12).exists { it > 10 } // Result: true
 Either.Right(7).exists { it > 10 }  // Result: false

 val left: Either<Int, Int> = Left(12)
 left.exists { it > 10 }      // Result: false
}
