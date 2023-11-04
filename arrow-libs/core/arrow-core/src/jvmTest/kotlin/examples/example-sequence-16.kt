// This file was automatically generated from Sequence.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSequence16

import arrow.core.widen

fun main() {
  val original: Sequence<String> = sequenceOf("Hello World")
  val result: Sequence<CharSequence> = original.widen()
}
