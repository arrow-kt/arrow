// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm53

import arrow.fx.stm.TMap
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tmap = TMap.new<Int, String>()
  val result = atomically {
    tmap[2] = "Hello"
    tmap.update(2) { it.reversed() }
    tmap[2]
  }
  //sampleEnd
  println("Result $result")
}
