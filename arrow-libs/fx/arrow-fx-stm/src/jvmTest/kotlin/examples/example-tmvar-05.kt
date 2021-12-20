// This file was automatically generated from TMVar.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTmvar05

import arrow.fx.stm.TMVar
import arrow.fx.stm.atomically

suspend fun main() {
  //sampleStart
  val tmvar = TMVar.empty<Int>()
  atomically {
    tmvar.put(20)
  }
  //sampleEnd
  println("New value ${atomically { tmvar.tryTake() } }")
}
