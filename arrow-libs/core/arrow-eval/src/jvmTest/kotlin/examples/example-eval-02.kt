// This file was automatically generated from Eval.kt by Knit tool. Do not edit.
package arrow.eval.examples.exampleEval02

import arrow.eval.*

fun main() {
  val eager = Eval.now(1).map { it + 1 }
  println(eager.value())
}
