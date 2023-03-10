// This file was automatically generated from Ior.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleIor16

import arrow.core.*

fun main(args: Array<String>) {
  //sampleStart
  val string: Ior<Int, String> = Ior.Right("Hello")
  val chars: Ior<Int, CharSequence> =
    string.widen<Int, CharSequence, String>()
  //sampleEnd
  println(chars)
}
