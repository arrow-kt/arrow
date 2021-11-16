// This file was automatically generated from ParTraverseResult.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.examplePartraverseresult01

import arrow.core.*
import arrow.typeclasses.Semigroup
import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

typealias Task = suspend () -> ResultNel<Throwable, Unit>

suspend fun main(): Unit {
  //sampleStart
  fun getTask(id: Int): Task =
    suspend { Result.catchNel { println("Working on task $id on ${Thread.currentThread().name}") } }

  val res = listOf(1, 2, 3)
    .map(::getTask)
    .parSequenceResult(Dispatchers.IO.nonEmptyList())
  //sampleEnd
  println(res)
}
