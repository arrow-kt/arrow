// This file was automatically generated from ParTraverseValidated.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.examplePartraversevalidated01

import arrow.core.*
import arrow.typeclasses.Semigroup
import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

typealias Task = suspend () -> ValidatedNel<Throwable, Unit>

suspend fun main(): Unit {
  //sampleStart
  fun getTask(id: Int): Task =
    suspend { Validated.catchNel { println("Working on task $id on ${Thread.currentThread().name}") } }

  val res = listOf(1, 2, 3)
    .map(::getTask)
    .parSequenceValidated(Dispatchers.IO, Semigroup.nonEmptyList())
  //sampleEnd
  println(res)
}
