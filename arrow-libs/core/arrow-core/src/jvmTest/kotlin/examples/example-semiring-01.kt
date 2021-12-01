// This file was automatically generated from Semiring.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSemiring01

import arrow.typeclasses.Semiring

fun main(args: Array<String>) {
  val result =
  //sampleStart
  Semiring.int().run { 1.combine(2) }
  //sampleEnd
  println(result)
}
