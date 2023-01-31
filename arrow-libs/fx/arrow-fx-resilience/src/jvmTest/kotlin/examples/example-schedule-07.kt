// This file was automatically generated from Schedule.kt by Knit tool. Do not edit.
package arrow.fx.resilience.examples.exampleSchedule07

import arrow.fx.resilience.*

suspend fun main(): Unit {
  var counter = 0
  //sampleStart
  val res = Schedule.doWhile<Int>{ it <= 3 }.repeat {
    println("Run: ${counter++}"); counter
  }
  //sampleEnd
  println(res)
}
