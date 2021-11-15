// This file was automatically generated from TVar.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleArrow01

import arrow.fx.stm.TVar

suspend fun main() {
  //sampleStart
  val tvar = TVar.new(10)
  val result = tvar.unsafeRead()
  //sampleEnd
  println(result)
}
