// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm54

import arrow.fx.stm.TMap
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tmap = TMap.new<Int, String>()
  atomically {
    tmap[1] = "Hello"
    tmap.remove(1)
  }
  //sampleEnd
}
