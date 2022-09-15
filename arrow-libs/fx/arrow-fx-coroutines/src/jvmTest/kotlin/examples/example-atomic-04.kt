// This file was automatically generated from Atomic.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleAtomic04

import arrow.fx.coroutines.*

suspend fun main() {
  val count = Atomic(0)
  (0 until 20_000).parMap {
    count.update(Int::inc)
  }
  println(count.get())
}
