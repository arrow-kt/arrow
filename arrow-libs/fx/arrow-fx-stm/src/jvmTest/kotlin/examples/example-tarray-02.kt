// This file was automatically generated from TArray.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTarray02

import arrow.fx.stm.TArray
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tarr = TArray.new(size = 10, 2)
  val result = atomically {
    tarr[5]
  }
  //sampleEnd
  println("Result $result")
}
