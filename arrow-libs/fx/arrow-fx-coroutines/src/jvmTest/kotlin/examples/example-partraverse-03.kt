// This file was automatically generated from ParTraverse.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.examplePartraverse03

import arrow.fx.coroutines.*

data class User(val id: Int, val createdOn: String)

suspend fun main(): Unit {
  //sampleStart
  suspend fun getUserById(id: Int): User =
    User(id, Thread.currentThread().name)

  val res = listOf(1, 2, 3)
    .parTraverse { getUserById(it) }
 //sampleEnd
 println(res)
}
