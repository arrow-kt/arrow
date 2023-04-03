// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither13

import arrow.core.Either

val r : Either<Int, Int> = Either.Right(7)
val rightMapLeft = r.mapLeft {it + 1}
val l: Either<Int, Int> = Either.Left(7)
val leftMapLeft = l.mapLeft {it + 1}
fun main() {
 println("rightMapLeft = $rightMapLeft")
 println("leftMapLeft = $leftMapLeft")
}
