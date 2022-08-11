// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither40

import arrow.core.Either.Left
import arrow.core.Either.Right

val right = Right(12).orNull() // Result: 12
val left = Left(12).orNull()   // Result: null
fun main() {
  println("right = $right")
  println("left = $left")
}
