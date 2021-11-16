// This file was automatically generated from TQueue.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTqueue04

import arrow.fx.stm.TQueue
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tq = TQueue.new<Int>()
  val result = atomically {
    tq.tryRead()
  }
  //sampleEnd
  println("Result $result")
  println("Items in queue ${atomically { tq.flush() }}")
}
