// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm57

import arrow.fx.stm.TSet
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tset = TSet.new<String>()
  atomically {
    tset += "Hello"
  }
  //sampleEnd
}
