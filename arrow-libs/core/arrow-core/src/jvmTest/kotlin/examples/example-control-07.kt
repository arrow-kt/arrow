// This file was automatically generated from Control.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleControl07

import arrow.core.continuations.control
import arrow.core.identity
import io.kotest.matchers.shouldBe

private val default = "failed"
suspend fun main() {
  val result = Result.success(1)
  control<String, Int> {
    val x: Int = result.bind { _: Throwable -> default }
    x
  }.fold({ default }, ::identity) shouldBe result.getOrElse { default }
}
