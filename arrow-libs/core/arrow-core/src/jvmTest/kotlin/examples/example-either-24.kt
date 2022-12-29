// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither24

import arrow.core.Either
import arrow.core.left

val y : Either<Int, Int> = 7.left()
val fold = y.fold({ 1 }, { it + 3 })
fun main() {
 println("fold = $fold")
}
