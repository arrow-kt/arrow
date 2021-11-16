// This file was automatically generated from Eval.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEval04

import arrow.core.*

fun main() {
  val alwaysEvaled = Eval.always { "expensive computation" }
  println(alwaysEvaled.value())
}
