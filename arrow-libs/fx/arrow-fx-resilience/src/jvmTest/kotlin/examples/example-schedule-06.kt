// This file was automatically generated from Schedule.kt by Knit tool. Do not edit.
package arrow.fx.resilience.examples.exampleSchedule06

import arrow.fx.resilience.*

suspend fun main(): Unit {
  var counter = 0
  //sampleStart
  val res = (Schedule.collect<Int>() zipLeft Schedule.recurs(3)).repeat {
    println("Run: ${counter++}")
    counter
  }
  // equal to
  val res2 = (Schedule.recurs<Int>(3) zipRight Schedule.collect<Int>()).repeat {
    println("Run: ${counter++}")
    counter
  }
  //sampleEnd
  println(res)
  println(res2)
}
