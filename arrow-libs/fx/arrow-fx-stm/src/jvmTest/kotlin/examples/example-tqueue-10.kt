// This file was automatically generated from TQueue.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTqueue10

import arrow.fx.stm.TQueue
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tq = TQueue.new<Int>()
  atomically {
    tq.write(0)
    tq.removeAll { it != 0 }
  }
  //sampleEnd
  println("Items in queue ${atomically { tq.flush() }}")
}
