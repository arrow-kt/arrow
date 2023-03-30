// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither20

import arrow.core.Either
import arrow.core.getOrElse

val r: Either<Throwable, Int> = Either.Left(NumberFormatException())
val httpStatusCode = r.getOrElse {
  when(it) {
    is NumberFormatException -> 400
    else -> 500
  }
}
fun main() {
 println("httpStatusCode = $httpStatusCode")
}
