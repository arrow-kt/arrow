// This file was automatically generated from Atomic.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleAtomic02

import arrow.fx.coroutines.*

typealias Id = Int
data class Job(val description: String)

val initialState = (0 until 10).map { i -> Pair(i, Job("Task #$i")) }

suspend fun main(): Unit {
  val jobs = Atomic(initialState)

  val batch = jobs.modify { j ->
    val batch = j.take(5)
    Pair(j.drop(5), batch)
  }

  batch.forEach { (id, job) ->
    println("Going to work on $job with id $id\n")
  }

  println("Remaining: ${jobs.get()}")
}
