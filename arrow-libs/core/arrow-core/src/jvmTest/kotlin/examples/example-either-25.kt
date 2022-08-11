// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither25

import arrow.core.Either
import arrow.core.getOrHandle

val r: Either<Throwable, Int> = Either.Left(NumberFormatException())
val httpStatusCode = r.getOrHandle {
  when(it) {
    is NumberFormatException -> 400
    else -> 500
  }
}
fun main() {
 println("httpStatusCode = $httpStatusCode")
}
