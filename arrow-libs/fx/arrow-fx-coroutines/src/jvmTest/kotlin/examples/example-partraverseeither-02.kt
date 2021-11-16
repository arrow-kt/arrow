// This file was automatically generated from ParTraverseEither.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.examplePartraverseeither02

import arrow.core.*
import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

object Error
data class User(val id: Int, val createdOn: String)

suspend fun main(): Unit {
  //sampleStart
  suspend fun getUserById(id: Int): Either<Error, User> =
    if(id == 4) Error.left()
    else User(id, Thread.currentThread().name).right()

  val res = listOf(1, 2, 3)
    .parTraverseEither(Dispatchers.IO) { getUserById(it) }

  val res2 = listOf(1, 4, 2, 3)
    .parTraverseEither(Dispatchers.IO) { getUserById(it) }
 //sampleEnd
 println(res)
 println(res2)
}
