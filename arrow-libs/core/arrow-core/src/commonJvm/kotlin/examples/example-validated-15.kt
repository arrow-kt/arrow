// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated15

import arrow.core.*

fun main(args: Array<String>) {
  val result =
  //sampleStart
  Validated.lift { s: CharSequence -> "$s World" }("Hello".valid())
  //sampleEnd
  println(result)
}
