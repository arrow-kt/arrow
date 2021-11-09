// This file was automatically generated from Cont.kt by Knit tool. Do not edit.
package example.exampleCont01

import arrow.core.*
import arrow.cont
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
  cont<String, Int> {
    val x = Either.Right(1).bind()
    val y = Validated.Valid(2).bind()
    val z = Option(3).bind { "Option was empty" }
    x + y + z
  }.fold(::println, ::println)

  cont<String, Int> {
    val x = Either.Right(1).bind()
    val y = Validated.Valid(2).bind()
    val z: Int = None.bind { "Option was empty" }
    x + y + z
  }.fold(::println, ::println)
}
