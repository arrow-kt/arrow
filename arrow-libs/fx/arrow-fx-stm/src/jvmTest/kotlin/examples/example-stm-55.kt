// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm55

import arrow.fx.stm.TSet
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tset = TSet.new<String>()
  val result = atomically {
    tset.insert("Hello")
    tset.member("Hello")
  }
  //sampleEnd
  println("Result $result")
}
