// This file was automatically generated from TMap.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTmap02

import arrow.fx.stm.TMap
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tmap = TMap.new<Int, String>()
  atomically {
    tmap.insert(1, "Hello")
    tmap[2] = "World"
  }
  //sampleEnd
}
