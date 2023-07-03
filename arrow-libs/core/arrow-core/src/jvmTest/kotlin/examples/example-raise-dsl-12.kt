// This file was automatically generated from Raise.kt by Knit tool. Do not edit.
package arrow.core.examples.exampleRaiseDsl12

import arrow.core.raise.merge
import io.kotest.matchers.shouldBe
import kotlin.random.Random

fun test() {
  merge { if(Random.nextBoolean()) raise("failed") else "failed" } shouldBe "failed"
}
