// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect11

import arrow.core.continuations.effect
import arrow.core.continuations.fold
import io.kotest.assertions.fail
import io.kotest.matchers.collections.shouldBeIn
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

suspend fun main() {
  val errorA = "ErrorA"
  val errorB = "ErrorB"
  coroutineScope {
    effect<String, Int> {
      val fa = async<Int> { raise(errorA) }
      val fb = async<Int> { raise(errorB) }
      fa.await() + fb.await()
    }.fold(
      { error ->
        println(error)
        error shouldBeIn listOf(errorA, errorB)
      },
      { fail("Int can never be the result") }
    )
  }
}
