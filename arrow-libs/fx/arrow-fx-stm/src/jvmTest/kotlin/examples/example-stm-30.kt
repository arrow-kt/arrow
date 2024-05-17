// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm30

import arrow.fx.stm.TSemaphore
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tsem = TSemaphore.new(5)
  atomically {
    tsem.release(2)
  }
  //sampleEnd
  println("Permits remaining ${atomically { tsem.available() }}")
}
