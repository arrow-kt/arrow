// This file was automatically generated from TSemaphore.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTsemaphore03

import arrow.fx.stm.TSemaphore
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tsem = TSemaphore.new(5)
  atomically {
    tsem.release()
  }
  //sampleEnd
  println("Permits remaining ${atomically { tsem.available() }}")
}
