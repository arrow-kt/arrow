// This file was automatically generated from ParMap.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleParMap01

import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

typealias Task = suspend () -> Unit

suspend fun main(): Unit {
  //sampleStart
  fun getTask(id: Int): Task =
    suspend { println("Working on task $id on ${Thread.currentThread().name}") }

  val res = listOf(1, 2, 3)
    .map(::getTask)
    .parSequence(Dispatchers.IO)
  //sampleEnd
  println(res)
}
