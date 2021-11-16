// This file was automatically generated from TMVar.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTmvar02

import arrow.fx.stm.TMVar
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tmvar = TMVar.empty<Int>()
  val result = atomically {
    tmvar.tryTake()
  }
  //sampleEnd
  println("Result $result")
  println("New value ${atomically { tmvar.tryTake() } }")
}
