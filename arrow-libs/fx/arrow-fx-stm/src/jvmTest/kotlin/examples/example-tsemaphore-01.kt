// This file was automatically generated from TSemaphore.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTsemaphore01

import arrow.fx.stm.TSemaphore
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tsem = TSemaphore.new(5)
  atomically {
    // acquire one permit
    tsem.acquire()
    // acquire 3 permits
    tsem.acquire(3)
  }
  //sampleEnd
  println("Permits remaining ${atomically { tsem.available() }}")
}
