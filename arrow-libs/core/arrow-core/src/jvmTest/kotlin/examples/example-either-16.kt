// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither16

import arrow.core.Either.Left
import arrow.core.Either

val r: Either<String, Int> = Either.Right(7)
val swapped = r.swap()
fun main() {
 println("swapped = $swapped")
}
