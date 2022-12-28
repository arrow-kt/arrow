// This file was automatically generated from flow.kt by Knit tool. Do not edit.
package arrow.fx.resilience.examples.exampleFlow01

import kotlinx.coroutines.flow.*
import arrow.fx.resilience.*

suspend fun main(): Unit {
  var counter = 0
  val flow = flow {
   emit(counter)
   if (++counter <= 5) throw RuntimeException("Bang!")
  }
  //sampleStart
 val sum = flow.retry(Schedule.recurs(5))
   .reduce(Int::plus)
  //sampleEnd
  println(sum)
}
