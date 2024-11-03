// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm24

import arrow.fx.stm.TSemaphore
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tsem = TSemaphore.new(5)
  val result = atomically {
    tsem.available()
  }
  //sampleEnd
  println("Result $result")
  println("Permits remaining ${atomically { tsem.available() }}")
}
