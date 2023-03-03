// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither56

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val string: Either<Int, String> = "Hello".right()
  val chars: Either<Int, CharSequence> =
    string.widen<Int, CharSequence, String>()
  //sampleEnd
  println(chars)
}
