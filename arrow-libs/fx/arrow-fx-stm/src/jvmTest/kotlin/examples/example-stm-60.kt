// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm60

import arrow.fx.stm.atomically
import arrow.fx.stm.stm

suspend fun main() {
  //sampleStart
  val i = 4
  val result = atomically {
    stm {
      check(i <= 5) // This calls retry and aborts if i <= 5
      "Larger than 5"
    } orElse { "Smaller than or equal to 5" }
  }
  //sampleEnd
  println("Result $result")
}
