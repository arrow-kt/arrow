// This file was automatically generated from EagerEffectScope.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerEffectScope05

import arrow.core.continuations.eagerEffect
import arrow.core.identity
import io.kotest.matchers.shouldBe

private val default = "failed"
fun main() {
  val result = Result.success(1)
  eagerEffect<String, Int> {
    val x: Int = result.bind { _: Throwable -> default }
    x
  }.fold({ default }, ::identity) shouldBe result.getOrElse { default }
}
