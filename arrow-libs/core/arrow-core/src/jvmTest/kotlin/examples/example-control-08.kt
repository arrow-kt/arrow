// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleControl08

import arrow.core.None
import arrow.core.Option
import arrow.core.continuations.control
import arrow.core.getOrElse
import arrow.core.identity
import io.kotest.matchers.shouldBe

private val default = "failed"
suspend fun main() {
  val option: Option<Int> = None
  control<String, Int> {
    val x: Int = option.bind { default }
    x
  }.fold({ default }, ::identity) shouldBe option.getOrElse { default }
}
