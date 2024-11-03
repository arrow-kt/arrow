// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm20

import arrow.fx.stm.TMVar
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tmvar = TMVar.empty<Int>()
  val result = atomically {
    tmvar.tryRead()
  }
  //sampleEnd
  println("Result $result")
}
