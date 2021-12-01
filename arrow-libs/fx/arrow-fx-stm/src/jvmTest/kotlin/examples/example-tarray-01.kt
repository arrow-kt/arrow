// This file was automatically generated from TArray.kt by Knit tool. Do not edit.
package arrow.fx.stm.examples.exampleTarray01

import arrow.fx.stm.TArray
import arrow.fx.stm.atomically

suspend fun example() {
  //sampleStart
  // Create a size 10 array and fill it by using the construction function.
  TArray.new(10) { i -> i * 2 }
  // Create a size 10 array and fill it with a constant
  TArray.new(size = 10, 2)
  // Create an array from `vararg` arguments:
  TArray.new(5, 2, 10, 600)
  // Create an array from any iterable
  TArray.new(listOf(5,4,3,2))
  //sampleEnd
}
