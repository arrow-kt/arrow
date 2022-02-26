// This file was automatically generated from EffectScope.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectScope06

import arrow.core.continuations.effect
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
