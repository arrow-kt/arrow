// This file was automatically generated from Validated.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleValidated20

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val string: Validated<Int, String> = "Hello".invalid()
  val chars: Validated<Int, CharSequence> =
    string.widen<Int, CharSequence, String>()
  //sampleEnd
  println(chars)
}
