// This file was automatically generated from ParTraverse.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.examplePartraverse01

import arrow.fx.coroutines.*

typealias Task = suspend () -> Unit

suspend fun main(): Unit {
  //sampleStart
  fun getTask(id: Int): Task =
    suspend { println("Working on task $id on ${Thread.currentThread().name}") }

  val res = listOf(1, 2, 3)
    .map(::getTask)
    .parSequence()
  //sampleEnd
  println(res)
}
