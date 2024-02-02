// This file was automatically generated from Eval.kt by Knit tool. Do not edit.
package arrow.eval.examples.exampleEval04

import arrow.eval.*

fun main() {
  val alwaysEvaled = Eval.always { "expensive computation" }
  println(alwaysEvaled.value())
}
