// This file was automatically generated from Schedule.kt by Knit tool. Do not edit.
package arrow.fx.resilience.examples.exampleSchedule04

import arrow.fx.resilience.*

suspend fun main(): Unit {
  var counter = 0
  //sampleStart
  val res = (Schedule.unit<Unit>() zipLeft Schedule.recurs(3)).repeat {
    println("Run: ${counter++}")
  }
  // equal to
  val res2 = (Schedule.recurs<Unit>(3) zipRight Schedule.unit()).repeat {
    println("Run: ${counter++}")
  }
  //sampleEnd
  println(res)
  println(res2)
}
