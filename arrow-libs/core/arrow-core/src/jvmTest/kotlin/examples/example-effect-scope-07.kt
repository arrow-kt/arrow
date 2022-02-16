// This file was automatically generated from EffectScope.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectScope07

import arrow.core.None
import arrow.core.Option
import arrow.core.continuations.effect
import arrow.core.getOrElse
import arrow.core.identity
import io.kotest.matchers.shouldBe

private val default = "failed"
suspend fun main() {
  val option: Option<Int> = None
  effect<String, Int> {
    val x: Int = option.bind { default }
    x
  }.fold({ default }, ::identity) shouldBe option.getOrElse { default }
}
