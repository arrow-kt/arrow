// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm59

import arrow.fx.stm.atomically
import arrow.fx.stm.stm

suspend fun main() {
  //sampleStart
  val i = 4
  val result = atomically {
    stm {
      if (i == 4) retry()
      "Not 4"
    } orElse { "4" }
  }
  //sampleEnd
  println("Result $result")
}
