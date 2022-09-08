// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither33

import arrow.core.*

fun main() {
  fun possiblyFailingOperation(): Either.Right<Int> =
    Either.Right(1)
  //sampleStart
  val result: Either<Exception, Int> = possiblyFailingOperation()
  result.fold(
       { println("operation failed with $it") },
       { println("operation succeeded with $it") }
  )
  //sampleEnd
}
