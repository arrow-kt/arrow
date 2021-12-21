// This file was automatically generated from TSemaphore.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTsemaphore02

import arrow.fx.stm.TSemaphore
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tsem = TSemaphore.new(0)
  val result = atomically {
    tsem.tryAcquire()
  }
  //sampleEnd
  println("Result $result")
  println("Permits remaining ${atomically { tsem.available() }}")
}
