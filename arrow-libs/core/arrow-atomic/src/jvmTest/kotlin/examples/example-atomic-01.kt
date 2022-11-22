// This file was automatically generated from Atomic.kt by Knit tool. Do not edit.
package arrow.atomic.examples.exampleAtomic01

import arrow.atomic.Atomic
import arrow.atomic.update
import arrow.fx.coroutines.parMap

suspend fun main() {
  val count = Atomic(0)
  (0 until 20_000).parMap {
    count.update(Int::inc)
  }
  println(count.value)
}
