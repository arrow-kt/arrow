// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm06

import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val result = atomically {
    catch({ throw Throwable() }) { _ -> "caught" }
  }
  //sampleEnd
  println("Result $result")
}
