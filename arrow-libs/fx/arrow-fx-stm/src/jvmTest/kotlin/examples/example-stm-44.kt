// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm44

import arrow.fx.stm.TArray
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tarr = TArray.new(size = 10, 2)
  val result = atomically {
    tarr[5] = 3

    tarr[5]
  }
  //sampleEnd
  println("Result $result")
}
