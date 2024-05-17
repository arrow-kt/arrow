// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm28

import arrow.fx.stm.TSemaphore
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tsem = TSemaphore.new(0)
  val result = atomically {
    tsem.tryAcquire(3)
  }
  //sampleEnd
  println("Result $result")
  println("Permits remaining ${atomically { tsem.available() }}")
}
