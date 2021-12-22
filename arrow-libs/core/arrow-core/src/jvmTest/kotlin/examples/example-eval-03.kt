// This file was automatically generated from Eval.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEval03

import arrow.core.*

fun main() {
  val lazyEvaled = Eval.later { "expensive computation" }
  println(lazyEvaled.value())
}
