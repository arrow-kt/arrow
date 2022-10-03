// This file was automatically generated from EagerEffectScope.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEagerEffectScope06

import arrow.core.None
import arrow.core.Option
import arrow.core.continuations.eagerEffect
import arrow.core.getOrElse
import arrow.core.identity
import io.kotest.matchers.shouldBe

private val default = "failed"
fun main() {
  val option: Option<Int> = None
  eagerEffect<String, Int> {
    val x: Int = option.bind { default }
    x
  }.fold({ default }, ::identity) shouldBe option.getOrElse { default }
}
