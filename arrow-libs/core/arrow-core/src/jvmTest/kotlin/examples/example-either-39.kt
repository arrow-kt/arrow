// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither39

 import arrow.core.*

fun main() {
  Either.Right(12).tap { println("flower") } // Result: prints "flower" and returns: Right(12)
  Either.Left(12).tap { println("flower") }  // Result: Left(12)
}
