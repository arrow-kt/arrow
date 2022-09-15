// This file was automatically generated from parMap.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleparMap03

import arrow.fx.coroutines.*

data class User(val id: Int, val createdOn: String)

suspend fun main(): Unit {
  //sampleStart
  suspend fun getUserById(id: Int): User =
    User(id, Thread.currentThread().name)

  val res = listOf(1, 2, 3)
    .parMap { getUserById(it) }
 //sampleEnd
 println(res)
}
