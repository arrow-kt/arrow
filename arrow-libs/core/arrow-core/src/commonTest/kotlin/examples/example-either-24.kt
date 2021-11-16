// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither24

import arrow.core.Either
import arrow.core.right

val x : Either<Int, Int> = 7.right()
val fold = x.fold({ 1 }, { it + 3 })
fun main() {
 println("fold = $fold")
}
