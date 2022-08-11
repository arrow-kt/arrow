// This file was automatically generated from Semiring.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleSemiring02

import arrow.typeclasses.Semiring

fun main(args: Array<String>) {
  val result =
  //sampleStart
  Semiring.int().run { 2.combineMultiplicate(3) }
  //sampleEnd
  println(result)
}
