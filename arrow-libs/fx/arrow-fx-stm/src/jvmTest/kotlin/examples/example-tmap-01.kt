// This file was automatically generated from TMap.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTmap01

import arrow.fx.stm.TMap
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tmap = TMap.new<Int, String>()
  val result = atomically {
    tmap.set(1, "Hello")
    tmap[2] = "World"

    tmap.lookup(1) + tmap[2]
  }
  //sampleEnd
  println("Result $result")
}
