// This file was automatically generated from Eval.kt by Knit tool. Do not edit.
package arrow.eval.examples.exampleEval04

import arrow.eval.*

fun main() {
  val lazyEvaled = Eval.atMostOnce { "expensive computation" }
  println(lazyEvaled.value())
}
