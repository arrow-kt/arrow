// This file was automatically generated from TMVar.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTmvar01

import arrow.fx.stm.TMVar
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tmvar = TMVar.new(10)
  val result = atomically {
    tmvar.take()
  }
  //sampleEnd
  println("Result $result")
  println("New value ${atomically { tmvar.tryTake() } }")
}
