// This file was automatically generated from Shift.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleShift06

import arrow.core.continuations.effect
import arrow.core.continuations.fold
import arrow.core.identity
import io.kotest.matchers.shouldBe

private val default = "failed"
suspend fun main() {
  val result = Result.success(1)
  effect<String, Int> {
    val x: Int = result.bind { _: Throwable -> default }
    x
  }.fold({ default }, ::identity) shouldBe result.getOrElse { default }
}
