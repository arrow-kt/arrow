// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither04

import arrow.core.Either
import arrow.core.flatMap

val right: Either<String, Int> = Either.Right(5)
val value = right.flatMap{ Either.Right(it + 1) }
fun main() {
 println("value = $value")
}
