// This file was automatically generated from Effect.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectGuide12

import arrow.core.continuations.effect
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun main() {
  val errorA = "ErrorA"
  val errorB = "ErrorB"
  effect<String, Int> {
    coroutineScope<Int> {
      launch { shift(errorA) }
      launch { shift(errorB) }
      45
    }
  }.fold({ fail("Shift can never finish") }, ::println)
}
