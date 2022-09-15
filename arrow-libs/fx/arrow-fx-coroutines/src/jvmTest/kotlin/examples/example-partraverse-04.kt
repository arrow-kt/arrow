// This file was automatically generated from parMap.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.exampleparMap04

import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

data class User(val id: Int, val createdOn: String)

suspend fun main(): Unit {
  //sampleStart
  suspend fun getUserById(id: Int): User =
    User(id, Thread.currentThread().name)

  val res = listOf(1, 2, 3)
    .parMap(Dispatchers.IO) { getUserById(it) }
 //sampleEnd
 println(res)
}
