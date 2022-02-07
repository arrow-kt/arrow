// This file was automatically generated from EffectScope.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleEffectScope08

import arrow.core.Eval
import arrow.core.continuations.effect
import arrow.core.identity
import io.kotest.matchers.shouldBe

private val default = "failed"
suspend fun main() {
  val eval: Eval<Int> = Eval.now(5)
  effect<String, Int> {
    val x: Int = eval.bind()
    x
  }.fold({ default }, ::identity) shouldBe eval.value()
}
