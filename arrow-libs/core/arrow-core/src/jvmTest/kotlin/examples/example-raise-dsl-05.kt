// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl05

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.recover
import kotlinx.coroutines.delay
import io.kotest.matchers.shouldBe

suspend fun test() {
  val one: Result<Int> = Result.success(1)
  val failure: Result<Int> = Result.failure(RuntimeException("Boom!"))

  either {
    val x = one.bind { -1 }
    val y = failure.bind { failure: Throwable ->
      raise("Something bad happened: ${failure.message}")
    }
    val z = failure.recover { failure: Throwable ->
      delay(10)
      1
    }.bind { raise("Something bad happened: ${it.message}") }
    x + y + z
  } shouldBe Either.Left("Something bad happened: Boom!")
}
