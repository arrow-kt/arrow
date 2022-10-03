// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither05

import arrow.core.Either
import arrow.core.flatMap

val left: Either<String, Int> = Either.Left("Something went wrong")
val value = left.flatMap{ Either.Right(it + 1) }
fun main() {
 println("value = $value")
}
