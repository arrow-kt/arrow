// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffect12

import arrow.core.continuations.effect
import arrow.core.continuations.fold
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun main() {
  val errorA = "ErrorA"
  val errorB = "ErrorB"
  val int = 45
  effect<String, Int> {
    coroutineScope<Int> {
      launch { shift(errorA) }
      launch { shift(errorB) }
      int
    }
  }.fold({ fail("Shift can never finish") }, { it shouldBe int })
}
