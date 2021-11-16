// This file was automatically generated from Schedule.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleSchedule03

import arrow.fx.coroutines.*

suspend fun main(): Unit {
  var counter = 0
  //sampleStart
  val res = Schedule.recurs<Unit>(3).repeat {
    println("Run: ${counter++}")
  }
  //sampleEnd
  println(res)
}
