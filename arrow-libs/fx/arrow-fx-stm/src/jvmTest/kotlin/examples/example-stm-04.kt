// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm04

import arrow.fx.stm.atomically
import arrow.fx.stm.stm

suspend fun main() {
  //sampleStart
  val result = atomically {
    stm { retry() } orElse { "Alternative" }
  }
  //sampleEnd
  println("Result $result")
}
