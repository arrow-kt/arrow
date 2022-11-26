// This file was automatically generated from ParTraverseEither.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.examplePartraverseeither01

import arrow.core.*
import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

object Error
typealias Task = suspend () -> Either<Throwable, Unit>

suspend fun main(): Unit {
  //sampleStart
  fun getTask(id: Int): Task =
    suspend { Either.catch { println("Working on task $id on ${Thread.currentThread().name}") } }

  val res = listOf(1, 2, 3)
    .map(::getTask)
    .parSequenceEither(Dispatchers.IO)
  //sampleEnd
  println(res)
}
