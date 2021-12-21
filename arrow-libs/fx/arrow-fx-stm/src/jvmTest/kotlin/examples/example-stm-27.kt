// This file was automatically generated from STM.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleStm27

import arrow.fx.stm.TQueue
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tq = TQueue.new<Int>()
  atomically {
    tq.write(2)
  }
  //sampleEnd
  println("Items in queue ${atomically { tq.flush() }}")
}
