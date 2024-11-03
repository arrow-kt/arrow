// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm14

import arrow.fx.stm.TVar
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tvar = TVar.new(10)
  val result = atomically {
    tvar.swap(20)
  }
  //sampleEnd
  println("Result $result")
  println("New value ${tvar.unsafeRead()}")
}
