// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither37

import arrow.core.*

fun main() {
 Either.Right(12).mapLeft { "flower" } // Result: Right(12)
 Either.Left(12).mapLeft { "flower" }  // Result: Left("flower")
}
