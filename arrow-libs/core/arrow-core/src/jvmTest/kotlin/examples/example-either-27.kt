// This file was automatically generated from Either.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEither27

import arrow.core.Either.Right
import arrow.core.leftIfNull

fun main() {
  val value =
  //sampleStart
    Right(12).leftIfNull({ -1 })
  //sampleEnd
  println(value)
}
