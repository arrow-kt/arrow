// This file was automatically generated from ParTraverseValidated.kt by Knit tool. Do not edit.
package arrow.fx.coroutines.examples.examplePartraversevalidated02

import arrow.core.*
import arrow.typeclasses.Semigroup
import arrow.fx.coroutines.*
import kotlinx.coroutines.Dispatchers

object Error
data class User(val id: Int, val createdOn: String)

suspend fun main(): Unit {
  //sampleStart
  suspend fun getUserById(id: Int): ValidatedNel<Error, User> =
    if(id % 2 == 0) Error.invalidNel()
    else User(id, Thread.currentThread().name).validNel()

  val res = listOf(1, 3, 5)
    .parTraverseValidated(Dispatchers.IO, Semigroup.nonEmptyList()) { getUserById(it) }

  val res2 = listOf(1, 2, 3, 4, 5)
    .parTraverseValidated(Dispatchers.IO, Semigroup.nonEmptyList()) { getUserById(it) }
 //sampleEnd
 println(res)
 println(res2)
}
